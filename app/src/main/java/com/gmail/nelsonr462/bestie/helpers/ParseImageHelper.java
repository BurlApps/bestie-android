package com.gmail.nelsonr462.bestie.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ParseImageHelper {

    private static final String TAG = ParseImageHelper.class.getSimpleName();
    private List<com.makeramen.roundedimageview.RoundedImageView> mImageViewList;
    private ArrayList<Uri> mImageUriList = new ArrayList<>();
    private Context mContext;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mCheckNowLayout;
    private RelativeLayout mRootLayout;
    private int mNextPair;
    private ArrayList<ParseObject> mParseImageObjects = new ArrayList<>();
    protected int mUriPosition;

    public ParseImageHelper(List<com.makeramen.roundedimageview.RoundedImageView> imageViewList,
                            Context context, RelativeLayout loadingLayout, RelativeLayout checkNowLayout, RelativeLayout rootLayout){
        mImageViewList = imageViewList;
        mContext = context;
        mLoadingLayout = loadingLayout;
        mUriPosition = 0;
        mCheckNowLayout = checkNowLayout;
        mRootLayout = rootLayout;
    }


    public void pullVoteImages(final int pullType){
        mRootLayout.setEnabled(false);

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery(ParseConstants.CLASS_IMAGE);
        imageQuery.whereEqualTo(ParseConstants.KEY_ACTIVE, true);
        imageQuery.setLimit(50);
        imageQuery.whereEqualTo(ParseConstants.KEY_GENDER, currentUser.get(ParseConstants.KEY_INTERESTED));
        imageQuery.addAscendingOrder(ParseConstants.KEY_OBJECT_ID);
        imageQuery.whereNotEqualTo(ParseConstants.KEY_CREATOR, currentUser);
        imageQuery.whereNotEqualTo(ParseConstants.KEY_VOTERS, currentUser);

        imageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mRootLayout.setEnabled(true);
                if (e != null) {
                    Log.d(TAG, e.getMessage());
                    return;
                }

                if(list.size() < 10) {
                    mCheckNowLayout.setVisibility(View.VISIBLE);
                    for (int j = 0; j < mImageUriList.size(); j++) {
                        Log.d(TAG, "IMAGE ID:  "+mImageUriList.get(j)+"  /  OBJECT ID:  "+mParseImageObjects.get(j).getObjectId());

                    }
                    return;
                }

                for (int i = 0; i < list.size(); i++) {
                    if(!mParseImageObjects.contains(list.get(i))) {
                        ParseFile image = list.get(i).getParseFile(ParseConstants.KEY_IMAGE);
                        Uri imageUri = Uri.parse(image.getUrl());
                        mImageUriList.add(imageUri);
                        mParseImageObjects.add(list.get(i));
                    }
                }
                Log.d(TAG, "IMAGE URI LIST SIZE:   " + mImageUriList.size());
                for (int j = 0; j < mImageUriList.size(); j++) {
                    Log.d(TAG, "IMAGE ID:  "+mImageUriList.get(j)+"  /  OBJECT ID:  "+mParseImageObjects.get(j).getObjectId());

                }
                if (pullType == 0) {
                    loadImagesIntoViews();

                } else {
                    loadNextPair(mNextPair);
                }
            }
        });

    }


    public void loadImagesIntoViews() {
        int i = 0;
        while(i < mImageUriList.size() && i < mImageViewList.size()) {
            Picasso.with(mContext).load(mImageUriList.get(i)).into(mImageViewList.get(i));
            mUriPosition++;
            mLoadingLayout.setVisibility(View.INVISIBLE);
            Log.d(TAG, "LOAD FIRST IMAGES    :  URIPOSITION="+mUriPosition);
            i++;
        }

    }

    public void loadNextPair(int nextPair) {
        if(mImageUriList.size() == mUriPosition){
            if (mImageUriList.size() == 1) {
                return;
            }
            pullVoteImages(1);
            return;
        }

        if(mUriPosition < mImageUriList.size()) {
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).into(mImageViewList.get((nextPair == 0) ? 0 : 2));
            mUriPosition++;
        }
        if (mUriPosition < mImageUriList.size()) {
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).into(mImageViewList.get((nextPair == 0) ? 1 : 3));
            mUriPosition++;
        }

        mNextPair = (mNextPair == 0)? 1 : 0;

        Log.d(TAG, "LOAD NEXT PAIR    :  URIPOSITION=" + mUriPosition);


    }

    public void setImageVoted(int imagePosition) {
        // Increment votes, wins/losses
        ParseObject selectedImage;
        ParseObject losingImage;



        if(imagePosition + 1 < mParseImageObjects.size()) {
            selectedImage = mParseImageObjects.get(imagePosition);
            losingImage = mParseImageObjects.get((imagePosition % 2 == 0) ? imagePosition + 1 : imagePosition - 1);

            saveParseChanges(selectedImage, losingImage, true);
//            saveParseChanges(losingImage, selectedImage, false);
        } /*else {
            Toast.makeText(mContext, "No more images!", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void saveParseChanges(final ParseObject imageToModify, final ParseObject opponent, final boolean won) {
        ParseRelation<ParseUser> votedRelation = imageToModify.getRelation(ParseConstants.KEY_VOTERS);
        votedRelation.add(ParseUser.getCurrentUser());

        imageToModify.increment(ParseConstants.KEY_VOTES);
        imageToModify.increment(won? ParseConstants.KEY_WINS : ParseConstants.KEY_LOSSES);
        imageToModify.increment(ParseConstants.KEY_OPPONENTS, opponent.getNumber(ParseConstants.KEY_SCORE));
        imageToModify.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
//                    Toast.makeText(mContext, "Image changes save failed!", Toast.LENGTH_LONG).show();
                }
                if(!won) return;

                saveParseChanges(opponent, imageToModify, false);
            }
        });
    }

}
