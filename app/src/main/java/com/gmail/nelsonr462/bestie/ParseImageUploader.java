package com.gmail.nelsonr462.bestie;


import android.util.Log;

import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.gmail.nelsonr462.bestie.ui.MainActivity;
import com.parse.ConfigCallback;
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
                final ParseFile imgFile = new ParseFile("pic.jpeg", mediaFile);
                imgFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        parseImage.put(ParseConstants.KEY_CREATOR, ParseUser.getCurrentUser());
                        parseImage.put(ParseConstants.KEY_IMAGE_MAX_VOTES, mParseConfig.getInt("imageMaxVotes"));
                        parseImage.put(ParseConstants.KEY_GENDER, currentUser.get(ParseConstants.KEY_GENDER));
                        parseImage.put(ParseConstants.KEY_IMAGE, imgFile);

                        parseImage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                Log.d("IMAGE UPLOADER:   ", "SUCCESS");
                                BestieRankFragment.mActiveBatchImages.add(parseImage);
                                BestieRankFragment.mUploadGrid.setAdapter(
                                        new UploadGridAdapter(BestieRankFragment.mContext,
                                                BestieRankFragment.mActiveBatchImages));

                                if(BestieRankFragment.mUserBatch == null) {
                                    newBatch(parseImage);
                                } else {
                                    ParseRelation<ParseObject> imageRelation = BestieRankFragment.mUserBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
                                    imageRelation.add(parseImage);
                                    parseImage.put(ParseConstants.KEY_USER_BATCH, BestieRankFragment.mUserBatch);
                                    parseImage.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
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
        newBatch.put(ParseConstants.KEY_ACTIVE, "false");
        newBatch.put(ParseConstants.KEY_CREATOR, ParseUser.getCurrentUser());
        ParseRelation<ParseObject> imageRelation = newBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
        imageRelation.add(parseImage);
        newBatch.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseImage.put(ParseConstants.KEY_USER_BATCH, newBatch);
                parseImage.saveInBackground();
                if(e!=null) Log.d("NEWB UPLOADER_ERROR  :", e.getMessage());
                BestieRankFragment.mUserBatch = newBatch;
                BestieRankFragment.mUserBatch.saveInBackground();
            }
        });
    }


}
