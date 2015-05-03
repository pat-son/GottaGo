package com.patson.gottago;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

/**
 * Copyright 2015 Patrick Son
 */
public class GottaGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "VOHv2kopTvcpHac5xfA6GIh2OfdwQC0stKygvqGQ",
                "0vxA1TYil8yUwd52kz5xBE5Db0V8YvWtkFxI7oXp");
    }

}
