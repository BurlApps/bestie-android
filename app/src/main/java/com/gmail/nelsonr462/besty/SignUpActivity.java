package com.gmail.nelsonr462.besty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
    @Bind(R.id.signUpButton) Button mSignUpButton;
    @Bind(R.id.emailField) EditText mEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
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


    @OnClick(R.id.signUpButton)
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
            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
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
        mSignUpButton.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft)
                .duration(500)
                .playOn(mSignUpButton);
    }
}
