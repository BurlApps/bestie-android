package com.gmail.nelsonr462.besty;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.logoLabel) TextView mLogoLabel;
    @Bind(R.id.crownImageView) ImageView mCrownImageView;
    @Bind(R.id.loginLabel) TextView mLoginLabel;
    @Bind(R.id.signUpButton) Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        getConnection();
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
                        YoYo.with(Techniques.FadeInDown)
                                .duration(500)
                                .playOn(mLoginLabel);
                        YoYo.with(Techniques.FadeIn)
                                .duration(500)
                                .playOn(mSignUpButton);
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

    @OnClick(R.id.signUpButton)
    public void signUpUser (View v) {
        animateFromWelcome(1);
    }
    @OnClick(R.id.loginLabel)
    public void loginUser (View v) {
        animateFromWelcome(0);

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
                        YoYo.with(Techniques.FadeOutUp)
                                .duration(500)
                                .playOn(mLoginLabel);
                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(mSignUpButton);
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
