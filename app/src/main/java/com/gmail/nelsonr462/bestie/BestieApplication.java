package com.gmail.nelsonr462.bestie;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

/**
 * Created by nelson on 9/8/15.
 */
public class BestieApplication extends Application {
    public static final String TAG = BestieApplication.class.getSimpleName();

    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gdHNF1S0dyCeuwiV5otdlXLG7Uxt2LVk9SWvHmO9", "3rSjfg1v6kR8WrKIo2wDoViriKXlMYcwYLwgTvEM");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(getApplicationContext());

    }

}
