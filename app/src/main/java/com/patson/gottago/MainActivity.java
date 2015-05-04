package com.patson.gottago;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.patson.gottago.newlocation.NewLocationActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {

    protected RestroomLocation mNewLocation;
    protected GoogleMap mGoogleMap;
    protected HashSet<RestroomLocation> mRestroomLocations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleMap = mapFragment.getMap();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //TODO: Scan through the set of restroom locations and start the detail view
            }
        });
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
            mGoogleMap.setMyLocationEnabled(true);
        }
        else {
            //If user location unavailable, show UML
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.650363, -71.324020), 13));
            mGoogleMap.setMyLocationEnabled(false);
        }

        Intent intent = getIntent();
        mNewLocation = intent.getParcelableExtra("location");
        if(mNewLocation != null) {
            //do something with location
            Log.d("MainActivity", "Location received: " + mNewLocation.getObjectId());
            addNewMarker();
        }
    }

    public void addNewMarker() {
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mNewLocation.getLatitude(), mNewLocation.getLongitude()))
                        .title(mNewLocation.getName())
                        .snippet(mNewLocation.getNotes())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        );
        mNewLocation.setMarkerId(marker.getId());
        mRestroomLocations.add(mNewLocation);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
    }

    public void removeMarkers() {
        mGoogleMap.clear();
        mRestroomLocations.clear();
    }

    @OnClick(R.id.newLocationButton) void addNewLocation() {
        //If the user isn't logged in, send them to the login screen
        if ( ParseUser.getCurrentUser() == null ) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //otherwise go to the add page
        else {
            Intent intent = new Intent(this, NewLocationActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.gottaGoButton) void gottaGoButton() {
        GPSTracker gps = new GPSTracker(this);
        if(!gps.canGetLocation()) {
            //No location; can't use this feature.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.no_location_error_messsage))
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            ParseGeoPoint userLocation = new ParseGeoPoint(gps.getLatitude(), gps.getLongitude());
            ParseQuery<ParseObject> locationQuery = new ParseQuery<>("Location");
            locationQuery.setLimit(10);
            locationQuery.whereWithinMiles("coord", userLocation, 2);
            locationQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        //success
                        if (list.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(getString(R.string.empty_list_error_message))
                                    .setTitle(getString(R.string.error_title_sorry))
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            removeMarkers();
                            for (ParseObject object : list) {
                                RestroomLocation location = new RestroomLocation();
                                location.setObjectId(object.getObjectId());
                                location.setName(object.getString("name"));
                                location.setNotes(object.getString("notes"));
                                location.setType(object.getString("type"));
                                location.setCondition(object.getInt("condition"));
                                if (object.getBoolean("handicap")) location.setHandicap(1);
                                else location.setHandicap(0);
                                ParseGeoPoint geoPoint = object.getParseGeoPoint("coord");
                                location.setLatitude(geoPoint.getLatitude());
                                location.setLongitude(geoPoint.getLongitude());

                                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                                .title(location.getName())
                                                .snippet(location.getNotes())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                );
                                location.setMarkerId(marker.getId());
                                mRestroomLocations.add(location);
                            }
                            moveCameraToNewMarkers();
                        }
                    } else {
                        //print error
                        e.printStackTrace();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(getString(R.string.error_title_oops))
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

    public void moveCameraToNewMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (RestroomLocation location : mRestroomLocations) {
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        LatLngBounds bounds = builder.build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
