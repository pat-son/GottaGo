package com.patson.gottago.newlocation;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.patson.gottago.R;

public class LocationDetailsActivity extends ActionBarActivity {

    public double mLatitude;
    public double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra("latitude", 0);
        mLongitude = intent.getDoubleExtra("longitude", 0);

        Log.d("LocationDetailsActivity", mLatitude + " " + mLongitude);
    }

}
