package com.patson.gottago.newlocation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.patson.gottago.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LocationDetailsActivity extends ActionBarActivity {

    public double mLatitude;
    public double mLongitude;

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
