package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
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

import com.gmail.nelsonr462.bestie.BestieApplication;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;


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
    private int VOTE_BUTTON = 1;
    private int UPLOAD_BUTTON = 2;



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
        }

        mVoteButton.setOnClickListener(loginListener(VOTE_BUTTON));
        mUploadButton.setOnClickListener(loginListener(UPLOAD_BUTTON));

        BestieApplication.mMixpanel.track("Mobile.Onboard.Selection");

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

                CreateUser(userGender, interested);
                BestieConstants.UPLOAD_ONBOARDING_ACTIVE = true;
                BestieConstants.VOTE_ONBOARDING_ACTIVE = true;
                BestieConstants.ONBOARD_TAB_CHOICE = buttonType;

                JSONObject props = new JSONObject();
                try {
                    props.put("Next", (buttonType == 1)? "Vote" : "Upload");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                BestieApplication.mMixpanel.track("Mobile.Onboard.Finished", props);

            }
        };
    }

    private void CreateUser(final String gender, final String interested) {
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
                            BestieApplication.mMixpanel.track("Mobile.User.Failed Authentication");
                        } else {
                            Log.d(TAG, "PARSE LOGIN:     Login success");
                            JSONObject props = new JSONObject();
                            try {
                                props.put("Gender", ParseUser.getCurrentUser().getString(ParseConstants.KEY_GENDER));
                                props.put("Interested", ParseUser.getCurrentUser().getString(ParseConstants.KEY_INTERESTED));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                            BestieApplication.mMixpanel.registerSuperProperties(props);
                            BestieApplication.mMixpanel.track("Mobile.User.Registered");
                            MixpanelAPI.People people = BestieApplication.mMixpanel.getPeople();
                            people.identify(ParseUser.getCurrentUser().getObjectId());
                            people.set("ID", ParseUser.getCurrentUser().getObjectId());
                            people.set("Interested", ParseUser.getCurrentUser().get(ParseConstants.KEY_INTERESTED));
                            people.set("Gender", ParseUser.getCurrentUser().get(ParseConstants.KEY_GENDER));

                            people.initPushHandling(getString(R.string.google_sender_id));
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
