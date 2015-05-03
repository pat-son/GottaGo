package com.patson.gottago.newlocation;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.patson.gottago.R;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EnterAddressActivity extends ActionBarActivity {

    @InjectView(R.id.addressTextField) EditText mAddressTextField;
    @InjectView(R.id.cityTextField) EditText mCityTextField;
    @InjectView(R.id.countryTextField) EditText mCountryTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_address);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.continueToDetails) void continueToDetails() {
        String address = mAddressTextField.getText().toString().trim();
        String city = mCityTextField.getText().toString().trim();
        String country = mCountryTextField.getText().toString().trim();

        if (address.isEmpty() || city.isEmpty() || country.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.enter_address_error_message))
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            String addressString = address + ", " + city + ", " + country ;
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList;

            try {
                addressList = geocoder.getFromLocationName(addressString, 1);
                if (addressList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.no_location_error_message))
                            .setTitle(getString(R.string.error_title_oops))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Address location = addressList.get(0);

                    Intent intent = new Intent(this, LocationDetailsActivity.class);
                    intent.putExtra("latitude", location.getLatitude());
                    intent.putExtra("longitude", location.getLongitude());
                    startActivity(intent);
                }

            } catch (IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.geocoder_exception_error_message))
                        .setTitle(getString(R.string.error_title_oops))
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

}
