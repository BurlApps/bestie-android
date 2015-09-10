package com.gmail.nelsonr462.besty;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.signUpLabel) TextView mSignUpLabel;
    @Bind(R.id.usernameField) EditText mUsername;
    @Bind(R.id.passwordField) EditText mPassword;
    @Bind(R.id.letsGoButton) Button mSignUpButton;
    @Bind(R.id.emailField) EditText mEmail;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.facebookSignUpButton) LoginButton mFacebookLogin;

    protected CallbackManager mCallbackManager = CallbackManager.Factory.create();;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        mFacebookLogin.setReadPermissions("user_photos");
        mFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
//        if(!isNetworkAvailable()) {
//            mSignUpButton.setActivated(false);
//            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
//        }
    }


    @OnClick(R.id.letsGoButton)
    public void signUpUser (View v) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();

        username = username.trim();
        password = password.trim();
        email = email.trim();

        if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.signup_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            mProgressBar.setVisibility(View.VISIBLE);

            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if(e == null) {
                        // Successful signup
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

        }

    }


    private void transitionInViews() {
        mSignUpLabel.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mSignUpLabel);
        mUsername.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mUsername);
        mPassword.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mPassword);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mEmail);
        mSignUpButton.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mSignUpButton);
        mFacebookLogin.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn(mFacebookLogin);
    }
}
