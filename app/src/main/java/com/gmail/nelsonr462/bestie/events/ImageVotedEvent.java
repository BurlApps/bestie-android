package com.gmail.nelsonr462.bestie.events;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class ImageVotedEvent {
    public int mVoteCount;
    public boolean mFinished;

    public ImageVotedEvent(Object result) {
        JSONObject convertedResult = new JSONObject((Map) result);

        try {
            mVoteCount = convertedResult.getInt("userVotes");
            mFinished = convertedResult.getBoolean("finished");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
