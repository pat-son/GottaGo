package com.patson.gottago.newlocation;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.patson.gottago.GPSTracker;
import com.patson.gottago.LoginActivity;
import com.patson.gottago.MainActivity;
import com.patson.gottago.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewLocationActivity extends ActionBarActivity {

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.locationButton) void useMyLocation() {
        gps = new GPSTracker(this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Intent intent = new Intent(this, LocationDetailsActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.no_location_error_messsage))
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @OnClick(R.id.addressButton) void enterAddress() {
        Intent intent = new Intent(this, EnterAddressActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Override functionality of the back button to prevent navigation back to the login screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_location, menu);
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
