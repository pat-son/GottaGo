package com.patson.gottago.newlocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.patson.gottago.MainActivity;
import com.patson.gottago.R;
import com.patson.gottago.RestroomLocation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LocationDetailsActivity extends ActionBarActivity {

    public double mLatitude;
    public double mLongitude;
    public int mType = -1;
    public int mCondition = -1;
    public String mTypeName ="";

    @InjectView(R.id.nameTextField) EditText mNameTextField;
    @InjectView(R.id.otherNotesTextField) EditText mOtherNotes;
    @InjectView(R.id.typeRadioGroup) RadioGroup mTypeGroup;
    @InjectView(R.id.conditionRadioGroup) RadioGroup mConditionGroup;
    @InjectView(R.id.handicapCheckbox) CheckBox mHandicap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        setupUI(findViewById(R.id.parent));
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra("latitude", 0);
        mLongitude = intent.getDoubleExtra("longitude", 0);

    }

    @OnClick(R.id.acceptButton) void addLocation() {
        final String name = mNameTextField.getText().toString().trim();
        final String notes = mOtherNotes.getText().toString().trim();
        if (name.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.name_empty_error_message))
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (mType == -1 || mCondition == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.radio_unselected_error_message))
                    .setTitle(getString(R.string.error_title_oops))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            if (mType == 0) mTypeName = "Singles";
            else if (mType == 1) mTypeName = "Stalls";
            else if (mType == 2) mTypeName = "Both";

            ParseGeoPoint point = new ParseGeoPoint(mLatitude, mLongitude);

            final ParseObject newLocation = new ParseObject("Location");

            newLocation.put("name", name);
            newLocation.put("notes", notes);
            newLocation.put("type", mTypeName);
            newLocation.put("condition", mCondition);
            newLocation.put("handicap", mHandicap.isChecked());
            newLocation.put("coord", point);
            newLocation.put("userId", ParseUser.getCurrentUser().getObjectId());
            newLocation.put("strikes", 0);
            newLocation.put("likes", 0);

            newLocation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //success
                        //relate this post to the user who created it
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseRelation<ParseObject> relation = user.getRelation("posts");
                        relation.add(newLocation);
                        user.saveInBackground();

                        //Create a RestroomLocation object to send back to the map view
                        RestroomLocation restroomLocation = new RestroomLocation();
                        restroomLocation.setName(name);
                        restroomLocation.setNotes(notes);
                        restroomLocation.setObjectId(newLocation.getObjectId());
                        restroomLocation.setType(mTypeName);
                        restroomLocation.setCondition(mCondition);
                        restroomLocation.setLatitude(mLatitude);
                        restroomLocation.setLongitude(mLongitude);
                        if(mHandicap.isChecked())
                            restroomLocation.setHandicap(1);
                        else
                            restroomLocation.setHandicap(0);

                        //Return to map
                        Intent intent = new Intent(LocationDetailsActivity.this, MainActivity.class);
                        intent.putExtra("location", restroomLocation);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        //print error
                        e.printStackTrace();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LocationDetailsActivity.this);
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

    public void onTypeClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.singlesButton:
                if (checked)
                    mType = 0;
                    break;
            case R.id.stallsButton:
                if (checked)
                    mType = 1;
                    break;
            case R.id.bothButton:
                if (checked)
                    mType = 2;
                    break;
        }
    }

    public void onConditionClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    mCondition = 0;
                break;
            case R.id.radioButton2:
                if (checked)
                    mCondition = 1;
                break;
            case R.id.radioButton3:
                if (checked)
                    mCondition = 2;
                break;
            case R.id.radioButton4:
                if (checked)
                    mCondition = 3;
                break;
            case R.id.radioButton5:
                if (checked)
                    mCondition = 4;
                break;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LocationDetailsActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

}
