package com.gmail.nelsonr462.bestie;


import com.parse.ConfigCallback;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    public void newParseImage(File mediaFile) {
        File imageFile = mediaFile;
        final ParseObject parseImage = new ParseObject(ParseConstants.CLASS_IMAGE);
        ParseUser currentUser = ParseUser.getCurrentUser();

        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject currentUser, ParseException e) {
                parseImage.put(ParseConstants.KEY_CREATOR, ParseUser.getCurrentUser().getObjectId());
                parseImage.put(ParseConstants.KEY_IMAGE_MAX_VOTES, mParseConfig.getInt("imageMaxVotes"));
                parseImage.put(ParseConstants.KEY_GENDER, currentUser.get(ParseConstants.KEY_GENDER));
                parseImage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mImageList.add(parseImage);
                    }
                });
            }
        });



    }

    public void newBatch(){

    }


}
