package com.gmail.nelsonr462.bestie;

import android.app.Application;

import com.gmail.nelsonr462.bestie.helpers.FontOverride;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;


public class BestieApplication extends Application {
    public static final String TAG = BestieApplication.class.getSimpleName();
    public static MixpanelAPI mMixpanel;


    public void onCreate() {
        super.onCreate();
        mMixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_key));
        mMixpanel.identify(mMixpanel.getDistinctId());
        mMixpanel.track("Mobile.App.Open");


        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(getApplicationContext());

        FontOverride.setDefaultFont(this, "SERIF", "fonts/Bariol_Regular.ttf");
        FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/Bariol_Bold.ttf");
    }

}
