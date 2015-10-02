package com.gmail.nelsonr462.bestie;

import android.app.Application;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by nelson on 9/8/15.
 */
public class BestieApplication extends Application {
    public static final String TAG = BestieApplication.class.getSimpleName();

    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "q1NZZSGYNxaYIQq5dDNkMlD407fmm2Hq6BoXBzu4", "aA6IKoTDyboREj5gNfWQ2PasrmaaRYtMTUlugje0");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(getApplicationContext());

    }

}
