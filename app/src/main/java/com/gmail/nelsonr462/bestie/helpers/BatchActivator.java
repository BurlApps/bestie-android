package com.gmail.nelsonr462.bestie.helpers;

import android.widget.Toast;

import com.gmail.nelsonr462.bestie.BestieApplication;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class BatchActivator {
    public static void activateBatch(final boolean active) {
        BestieRankFragment.mUserBatch.put(ParseConstants.KEY_ACTIVE, active);
        BestieRankFragment.mUserBatch.put(ParseConstants.KEY_USER_VOTES, 0);
        ParseRelation<ParseObject> imageRelation = BestieRankFragment.mUserBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
        ParseQuery<ParseObject> imageQuery = imageRelation.getQuery();
        imageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e!=null) {
                    Toast.makeText(BestieRankFragment.mContext, "", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i <list.size(); i++) {
                    list.get(i).put(ParseConstants.KEY_ACTIVE, active);
                    list.get(i).saveInBackground();
                }

                JSONObject props = new JSONObject();
                try {
                    props.put("images", list.size());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                BestieApplication.mMixpanel.getPeople().increment("Batches", 1);
                BestieApplication.mMixpanel.track("Mobile.Batch.Create", props);
                BestieApplication.mMixpanel.timeEvent("Mobile.Batch.Results");
                BestieRankFragment.mUserBatch.saveInBackground();
            }
        });
    }


    public static void moveBatch() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.remove(ParseConstants.KEY_BATCH);
        ParseRelation<ParseObject> batchRelation = currentUser.getRelation(ParseConstants.KEY_BATCHES);
        batchRelation.add(BestieRankFragment.mUserBatch);
        currentUser.saveInBackground();
    }

}
