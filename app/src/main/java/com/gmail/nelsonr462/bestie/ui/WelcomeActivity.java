package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.R;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;


public class WelcomeActivity extends AppCompatActivity {
    private String TAG = WelcomeActivity.class.getSimpleName();

    private TextView mLogoLabel;
    private ImageView mCrownImage;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mProgressBar = (ProgressBar) findViewById(R.id.welcomeProgressBar);

        if(!isNetworkAvailable()) {
            connectionToast();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (e != null) {
                        Log.d(TAG, "PARSE LOGIN:     Login failed");
                    } else {
                        Log.d(TAG, "PARSE LOGIN:     Login success");
                        navigateToMain();
                    }
                }
            });
        }
    }



    private void navigateToMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void connectionToast() {
            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

}