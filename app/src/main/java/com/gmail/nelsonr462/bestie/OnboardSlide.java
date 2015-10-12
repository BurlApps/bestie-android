package com.gmail.nelsonr462.bestie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import com.gmail.nelsonr462.bestie.events.OnboardLoadEvent;
import com.gmail.nelsonr462.bestie.events.OnboardStopEvent;

import de.greenrobot.event.EventBus;


public class OnboardSlide extends Fragment{
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private View mView;

    public static OnboardSlide newInstance(int layoutResId) {
        OnboardSlide sampleSlide = new OnboardSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int layoutResId;

    public OnboardSlide() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView =  inflater.inflate(layoutResId, container, false);
        return  mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(layoutResId == R.layout.onboarding_slide_1)
            EventBus.getDefault().post(new OnboardLoadEvent(layoutResId));

    }

    @Override
    public void onPause() {
        if(layoutResId == R.layout.onboarding_slide_1)
            EventBus.getDefault().post(new OnboardStopEvent());
        super.onPause();
    }

    public TextSwitcher getSwitcher() {
        if (layoutResId == R.layout.onboarding_slide_1) {
            return (TextSwitcher) mView.findViewById(R.id.textSwitcher);
        } else {
            return null;
        }
    }
}
