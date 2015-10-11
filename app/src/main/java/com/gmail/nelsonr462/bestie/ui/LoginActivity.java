package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

    private Vibrator mVibrator;

    private ProgressBar mProgressBar;
    private RelativeLayout mSetPreferences;
    private Button mVoteButton;
    private Button mUploadButton;
    private CheckBox mSeeMale;
    private CheckBox mSeeFemale;
    private RadioButton mIAmMale;
    private RadioButton mIamFemale;
    private RadioGroup mIamRadioGroup;
    private int VOTE_BUTTON = 0;
    private int UPLOAD_BUTTON = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mSetPreferences = (RelativeLayout) findViewById(R.id.whoAmI);
        mVoteButton = (Button) findViewById(R.id.onboardStartVoting);
        mUploadButton = (Button) findViewById(R.id.onboardFindBestie);
        mIAmMale = (RadioButton) findViewById(R.id.iAmMale);
        mIamFemale = (RadioButton) findViewById(R.id.iAmFemale);
        mIamRadioGroup = (RadioGroup) findViewById(R.id.iAmRadioGroup);
        mSeeFemale = (CheckBox) findViewById(R.id.seeFemale);
        mSeeMale = (CheckBox) findViewById(R.id.seeMale);


        if(!isNetworkAvailable()) {
            connectionToast();
        } /*else {
            CreateUser();
        }*/

        mVoteButton.setOnClickListener(loginListener(VOTE_BUTTON));
        mUploadButton.setOnClickListener(loginListener(UPLOAD_BUTTON));


    }

    private View.OnClickListener loginListener(final int buttonType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIamRadioGroup.getCheckedRadioButtonId() == -1) {
                    mVibrator.vibrate(100);
                    new android.support.v7.app.AlertDialog.Builder(LoginActivity.this).setTitle("Woops!")
                            .setMessage("Please choose a gender!")
                            .setPositiveButton("Okay", null)
                            .show();
                    return;
                }

                String userGender;
                if(mIamRadioGroup.getCheckedRadioButtonId() == R.id.iAmFemale) {
                    userGender = "female";
                } else {
                    userGender = "male";
                }

                String interested;
                if(mSeeFemale.isChecked() && mSeeMale.isChecked()) {
                    interested = "both";
                } else if (mSeeFemale.isChecked()) {
                    interested = "female";
                } else if (mSeeMale.isChecked()) {
                    interested = "male";
                } else {
                    interested = "both";
                }

                CreateUser(userGender, interested, buttonType);


            }
        };
    }

    private void CreateUser(final String gender, final String interested, int buttonType) {
        mSetPreferences.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                parseUser.put(ParseConstants.KEY_GENDER, gender);
                parseUser.put(ParseConstants.KEY_INTERESTED, interested);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        if (e != null) {
                            Log.d(TAG, "PARSE LOGIN:     Login failed");
                        } else {
                            Log.d(TAG, "PARSE LOGIN:     Login success");
                            // Integrate choice of vote or upload
                            navigateToMain();
                        }
                    }
                });
            }
        });
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
