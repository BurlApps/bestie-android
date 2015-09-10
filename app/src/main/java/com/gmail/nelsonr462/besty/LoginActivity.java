package com.gmail.nelsonr462.besty;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.loginLabel) TextView mLoginLabel;
    @Bind(R.id.usernameField) EditText mUsername;
    @Bind(R.id.passwordField) EditText mPassword;
    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.facebookLoginButton) LoginButton mFacebookLoginButton;

    protected CallbackManager mCallbackManager = CallbackManager.Factory.create();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        mFacebookLoginButton.setReadPermissions("user_photos");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        transitionInViews();

    }

    @OnClick(R.id.loginButton)
    public void userLogin(View v) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        username = username.trim();
        password = password.trim();

        if(username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            //Login
            mProgressBar.setVisibility(View.VISIBLE);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (e == null) {
                        // Login Success
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.login_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

        }
    }

    private void transitionInViews() {
        mLoginLabel.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mLoginLabel);
        mUsername.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mUsername);
        mPassword.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mPassword);
        mLoginButton.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mLoginButton);
        mFacebookLoginButton.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mFacebookLoginButton);
    }
}
