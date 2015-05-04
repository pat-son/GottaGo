package com.patson.gottago;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MapDetailActivity extends ActionBarActivity {

    public RestroomLocation mLocation;

    private String[] conditions = {"Terrible", "Poor", "Fair", "Good", "Great"};

    @InjectView(R.id.nameTextView) TextView mNameTextView;
    @InjectView(R.id.addressTextView) TextView mAddressTextView;
    @InjectView(R.id.typeTextView) TextView mTypeTextView;
    @InjectView(R.id.conditionTextView) TextView mConditionTextView;
    @InjectView(R.id.handicapLabel) TextView mHandicapLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mLocation = intent.getParcelableExtra("location");
        if (mLocation.getHandicap() == 0) mHandicapLabel.setVisibility(View.INVISIBLE);
        mNameTextView.setText(mLocation.getName());

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            if (addressList.isEmpty()) {
                mAddressTextView.setText("Address could not be obtained.");
            }
            else {
                Address address = addressList.get(0);
                String streetAddress = address.getThoroughfare();
                String city = address.getLocality();
                if(city == null || city.isEmpty()) city = address.getSubLocality();
                String state = address.getAdminArea();
                mAddressTextView.setText(streetAddress + ", " + city + ", " + state);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
            builder.setMessage(e.getMessage())
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        mTypeTextView.setText(mLocation.getType());
        mConditionTextView.setText(conditions[mLocation.getCondition()]);

    }

    @OnClick(R.id.directionsButton) void getDirections() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + mLocation.getLatitude() + "," + mLocation.getLongitude()));
        startActivity(intent);
    }

    @OnClick(R.id.likeButton) void likeButtonPressed() {
        final ParseUser user = ParseUser.getCurrentUser();
        final ParseRelation<ParseObject> relation = user.getRelation("likedPosts");
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                boolean hasLiked = false;
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(getString(R.string.error_title_oops))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    for (ParseObject object : list) {
                        if (mLocation.getObjectId().equals(object.getObjectId())) hasLiked = true;
                    }
                    if (hasLiked) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                        builder.setMessage(getString(R.string.already_liked_error_message))
                                .setTitle(getString(R.string.error_title_sorry))
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        ParseQuery<ParseObject> query = new ParseQuery<>("Location");
                        query.whereEqualTo("objectId", mLocation.getObjectId());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(getString(R.string.error_title_oops))
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    for (ParseObject object : list) {
                                        object.increment("likes");
                                        object.saveInBackground();

                                        relation.add(object);
                                        user.saveInBackground();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @OnClick(R.id.reportButton) void reportButtonPressed() {
        final ParseUser user = ParseUser.getCurrentUser();
        final ParseRelation<ParseObject> relation = user.getRelation("reportedPosts");
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                boolean hasReported = false;
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(getString(R.string.error_title_oops))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    for (ParseObject object : list) {
                        if (mLocation.getObjectId().equals(object.getObjectId())) hasReported = true;
                    }
                    if (hasReported) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                        builder.setMessage("You have already reported this post!")
                                .setTitle(getString(R.string.error_title_sorry))
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        ParseQuery<ParseObject> query = new ParseQuery<>("Location");
                        query.whereEqualTo("objectId", mLocation.getObjectId());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapDetailActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(getString(R.string.error_title_oops))
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                else {
                                    for (ParseObject object : list) {
                                        object.increment("strikes");
                                        object.saveInBackground();

                                        relation.add(object);
                                        user.saveInBackground();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
