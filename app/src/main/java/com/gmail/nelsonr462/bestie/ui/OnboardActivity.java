package com.gmail.nelsonr462.bestie.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.gmail.nelsonr462.bestie.OnboardSlide;
import com.gmail.nelsonr462.bestie.R;

public class OnboardActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_1));
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_2));
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_3));


    }


    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
