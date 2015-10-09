package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class LoginActivity extends AppCompatActivity {
    private String TAG = LoginActivity.class.getSimpleName();

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

                    parseUser.put(ParseConstants.KEY_GENDER, ParseConstants.STRING_FEMALE);
                    parseUser.put(ParseConstants.KEY_INTERESTED, ParseConstants.STRING_MALE);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
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
            });
        }
    }



    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
