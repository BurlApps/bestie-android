package com.gmail.nelsonr462.besty;

import android.app.Application;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by nelson on 9/8/15.
 */
public class BestyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gdHNF1S0dyCeuwiV5otdlXLG7Uxt2LVk9SWvHmO9", "3rSjfg1v6kR8WrKIo2wDoViriKXlMYcwYLwgTvEM");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

}
