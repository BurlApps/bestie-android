package com.gmail.nelsonr462.bestie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.paolorotolo.appintro.AppIntro2;
import com.gmail.nelsonr462.bestie.OnboardSlide;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.events.OnboardLoadEvent;
import com.gmail.nelsonr462.bestie.events.OnboardStopEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class OnboardActivity extends AppIntro2 {
    private TextSwitcher mSwitcher;
    private int mCounter;
    private ArrayList<String> mText = new ArrayList<>();
    private final Handler h = new Handler();
    private final int delay = 2000; //milliseconds
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (mCounter > 3) mCounter = 0;
            mSwitcher.setText(mText.get(mCounter));
            mCounter++;
            h.postDelayed(this, delay);
        }
    };

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_1));
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_2));
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_3));
        addSlide(OnboardSlide.newInstance(R.layout.onboarding_slide_4));
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(OnboardLoadEvent event) {
        if(event.mLayoutId == R.layout.onboarding_slide_1) {

            OnboardSlide firstSlide = (OnboardSlide) getSlides().get(0);

            mCounter = 0;

            mText.add(0, "Instagram");
            mText.add(1, "Tinder");
            mText.add(2, "Facebook");
            mText.add(3, "Twitter");

            mSwitcher = firstSlide.getSwitcher();
            mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    TextView text =  new TextView(OnboardActivity.this);
                    text.setTextColor(getResources().getColor(R.color.bestieMessageText));
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    text.setTextColor(getResources().getColor(R.color.bestieRed));

                    return text;
                }
            });


            Animation in = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            mSwitcher.setInAnimation(in);
            mSwitcher.setOutAnimation(out);


            h.post(r);
        }

    }

    public void onEvent(OnboardStopEvent event) {
        h.removeCallbacks(r);
    }
}
