package com.gmail.nelsonr462.bestie.events;


public class OnboardLoadEvent {
    public boolean mLoaded;
    public int mLayoutId;

    public OnboardLoadEvent(int id) {
        mLoaded = true;
        mLayoutId = id;
    }
}
