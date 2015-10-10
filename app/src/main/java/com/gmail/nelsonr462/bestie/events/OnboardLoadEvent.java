package com.gmail.nelsonr462.bestie.events;

/**
 * Created by nelson on 10/9/15.
 */
public class OnboardLoadEvent {
    public boolean mLoaded;
    public int mLayoutId;

    public OnboardLoadEvent(int id) {
        mLoaded = true;
        mLayoutId = id;
    }
}
