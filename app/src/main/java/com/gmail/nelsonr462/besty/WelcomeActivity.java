package com.gmail.nelsonr462.besty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nineoldandroids.animation.Animator;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    private Dialog progressDialog;

    @Bind(R.id.logoLabel) TextView mLogoLabel;
    @Bind(R.id.crownImageView) ImageView mCrownImageView;
    @Bind(R.id.fbLoginButton) LoginButton mLoginButton;


    protected CallbackManager mCallbackManager = CallbackManager.Factory.create();;


//    @Bind(R.id.loginLabel) TextView mLoginLabel;
//    @Bind(R.id.signUpButton) Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        getConnection();

//        mLoginButton.setReadPermissions("user_photos");
//        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        YoYo.with(Techniques.FadeInDown)
                                .duration(500)
                                .playOn(mCrownImageView);
//                        YoYo.with(Techniques.FadeInDown)
//                                .duration(500)
//                                .playOn(mLoginLabel);
//                        YoYo.with(Techniques.FadeIn)
//                                .duration(500)
//                                .playOn(mSignUpButton);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .playOn(mLogoLabel);
    }

//    @OnClick(R.id.signUpButton)
//    public void signUpUser (View v) {
//        animateFromWelcome(1);
//    }
//    @OnClick(R.id.loginLabel)
//    public void loginUser (View v) {
//        animateFromWelcome(0);
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


    // Facebook Button OnClick Listener

    @OnClick(R.id.fbLoginButton)
    public void onLoginClick(View v) {
//        progressDialog = ProgressDialog.show(WelcomeActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
//                progressDialog.dismiss();
                if (user == null) {
                    Log.d(BestyApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(BestyApplication.TAG, "User signed up and logged in through Facebook!");
                    navigateToMain();
                } else {
                    Log.d(BestyApplication.TAG, "User logged in through Facebook!");
                    navigateToMain();
                }
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void getConnection() {
        if(!isNetworkAvailable()) {
            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();
        }
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

    private void animateFromWelcome(final int intentType) {
        final Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
        final Intent signUpIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);

        YoYo.with(Techniques.FadeOutUp)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        YoYo.with(Techniques.FadeOutUp)
                                .duration(500)
                                .playOn(mCrownImageView);
//                        YoYo.with(Techniques.FadeOutUp)
//                                .duration(500)
//                                .playOn(mLoginLabel);
//                        YoYo.with(Techniques.FadeOut)
//                                .duration(500)
//                                .playOn(mSignUpButton);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (intentType == 0) {
                            startActivity(loginIntent);
                        } else {
                            startActivity(signUpIntent);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(mLogoLabel);
    }
}
