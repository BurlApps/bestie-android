package com.gmail.nelsonr462.bestie.helpers;


import android.widget.Toast;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.events.BatchUpdateEvent;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ParseImageUploader {
    private File mImageFile;
    private ParseConfig mParseConfig;
    ArrayList<ParseObject> mImageList;


    public ParseImageUploader(ParseConfig parseConfig){
        mImageList = new ArrayList<>();
        mParseConfig = parseConfig;

    }

    public void newParseImage(final byte[] mediaFile) {
        final ParseObject parseImage = new ParseObject(ParseConstants.CLASS_IMAGE);
        ParseUser currentUser = ParseUser.getCurrentUser();

        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject currentUser, ParseException e) {
                final ParseFile imgFile = new ParseFile("pic.jpeg", mediaFile, "image/jpeg");
                imgFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Toast.makeText(BestieRankFragment.mContext, "Image save failed!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        parseImage.put(ParseConstants.KEY_CREATOR, ParseUser.getCurrentUser());
                        parseImage.put(ParseConstants.KEY_MAX_VOTES_BATCH, mParseConfig.getInt(ParseConstants.KEY_IMAGE_MAX_VOTES));
                        parseImage.put(ParseConstants.KEY_GENDER, currentUser.get(ParseConstants.KEY_GENDER));
                        parseImage.put(ParseConstants.KEY_IMAGE, imgFile);

                        parseImage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                BestieRankFragment.mActiveBatchImages.add(parseImage);
                                BestieRankFragment.mUploadGrid.setAdapter(
                                        new UploadGridAdapter(BestieRankFragment.mContext,
                                                BestieRankFragment.mActiveBatchImages));

                                if(BestieRankFragment.mUserBatch == null ||
                                        BestieRankFragment.mUserBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH) == BestieRankFragment.mUserBatch.getInt(ParseConstants.KEY_VOTES)) {
                                    newBatch(parseImage);

                                } else {
                                    ParseRelation<ParseObject> imageRelation = BestieRankFragment.mUserBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
                                    imageRelation.add(parseImage);
                                    parseImage.put(ParseConstants.KEY_BATCH, BestieRankFragment.mUserBatch);
                                    parseImage.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            BestieRankFragment.mUserBatch.increment(ParseConstants.KEY_MAX_VOTES_BATCH, mParseConfig.getInt(ParseConstants.KEY_IMAGE_MAX_VOTES));
                                            BestieRankFragment.mUserBatch.saveInBackground();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

            }
        });



    }

    public void newBatch(final ParseObject parseImage){
        final ParseObject newBatch = new ParseObject(ParseConstants.CLASS_BATCH);
        newBatch.put(ParseConstants.KEY_ACTIVE, false);
        newBatch.put(ParseConstants.KEY_USER_VOTES, 0);
        newBatch.put(ParseConstants.KEY_VOTES, 0);
        newBatch.increment(ParseConstants.KEY_MAX_VOTES_BATCH, mParseConfig.getInt(ParseConstants.KEY_IMAGE_MAX_VOTES));
        newBatch.put(ParseConstants.KEY_CREATOR, ParseUser.getCurrentUser());
        ParseRelation<ParseObject> imageRelation = newBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
        imageRelation.add(parseImage);
        newBatch.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(BestieRankFragment.mContext, "Image save failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                parseImage.put(ParseConstants.KEY_BATCH, newBatch);
                parseImage.saveInBackground();
                BestieRankFragment.mUserBatch = newBatch;
                BestieRankFragment.mUserBatch.saveInBackground();
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject currentUser, ParseException e) {
                        currentUser.put(ParseConstants.KEY_BATCH, BestieRankFragment.mUserBatch);
                        currentUser.saveInBackground();
                    }
                });
                EventBus.getDefault().post(new BatchUpdateEvent(newBatch));

            }
        });
    }


}
