package com.gmail.nelsonr462.bestie;

import android.app.Application;

import com.gmail.nelsonr462.bestie.helpers.FontOverride;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;


public class BestieApplication extends Application {
    public static final String TAG = BestieApplication.class.getSimpleName();

    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "q1NZZSGYNxaYIQq5dDNkMlD407fmm2Hq6BoXBzu4", "aA6IKoTDyboREj5gNfWQ2PasrmaaRYtMTUlugje0");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(getApplicationContext());

        FontOverride.setDefaultFont(this, "SERIF", "fonts/Bariol_Regular.ttf");
        FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/Bariol_Bold.ttf");

    }

}
