package com.gmail.nelsonr462.bestie;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.gmail.nelsonr462.bestie.helpers.FontOverride;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;


public class BestieApplication extends Application {
    public static final String TAG = BestieApplication.class.getSimpleName();
    public static MixpanelAPI mMixpanel;


    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        mMixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_key));
        mMixpanel.identify(mMixpanel.getDistinctId());
        mMixpanel.track("Mobile.App.Open");


        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();

        FontOverride.setDefaultFont(this, "SERIF", "fonts/Bariol_Regular.ttf");
        FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/Bariol_Bold.ttf");
    }

}
