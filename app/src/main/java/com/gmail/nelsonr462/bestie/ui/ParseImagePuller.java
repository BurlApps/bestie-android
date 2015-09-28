package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ParseImagePuller {

    private static final String TAG = ParseImagePuller.class.getSimpleName();
    private List<com.makeramen.roundedimageview.RoundedImageView> mImageViewList;
    private ArrayList<Uri> mImageUriList = new ArrayList<>();
    private Context mContext;
    private RelativeLayout mLoadingLayout;
    private int mUriPosition;

    public ParseImagePuller(List<com.makeramen.roundedimageview.RoundedImageView> imageViewList, Context context, RelativeLayout loadingLayout){
        mImageViewList = imageViewList;
        mContext = context;
        mLoadingLayout = loadingLayout;
        mUriPosition = 0;
    }


    public void pullVoteImages(final int pullType){

        if(pullType == 1) {
            clearSeenUris();
        }


        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery(ParseConstants.CLASS_IMAGE);
//        imageQuery.whereEqualTo(ParseConstants.KEY_ACTIVE, true);
        imageQuery.setLimit(10);
//        imageQuery.whereEqualTo(ParseConstants.KEY_GENDER, currentUser.get(ParseConstants.KEY_INTERESTED));
        imageQuery.addAscendingOrder(ParseConstants.KEY_OBJECT_ID);
//        imageQuery.whereNotEqualTo(ParseConstants.KEY_CREATOR, currentUser);
//        imageQuery.whereNotEqualTo(ParseConstants.KEY_VOTERS, currentUser);
        imageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if( e != null) {
                    Log.d(TAG, e.getMessage());
                    return;
                }


                for(int i = 0; i< list.size(); i++) {
                    Log.d(TAG, "IMAGE ID:    "+list.get(i).getObjectId());
                    ParseFile image = list.get(i).getParseFile(ParseConstants.KEY_IMAGE);
                    Uri imageUri = Uri.parse(image.getUrl());
                    mImageUriList.add(imageUri);
                }
                Log.d(TAG, "IMAGE URI LIST SIZE:   " + mImageUriList.size());
                if(pullType == 0) {
                    loadImagesIntoViews();

                }
            }
        });

    }

    public void loadImagesIntoViews() {
        for(int i = 0; i < mImageViewList.size(); i++) {
            Picasso.with(mContext).load(mImageUriList.get(i)).into(mImageViewList.get(i));
            mUriPosition++;
            mLoadingLayout.setVisibility(View.INVISIBLE);
//            mImageViewList.get(i).setImageURI(mImageUriList.get(i));
        }
    }

    public void loadNextPair(LinearLayout votingPair) {
        if((mImageUriList.size() - mUriPosition) <= 4) {
            pullVoteImages(1);
        }
        for(int i = 0; i < votingPair.getChildCount(); i++) {
            RoundedImageView votingImage = (RoundedImageView) votingPair.getChildAt(i);
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).into(votingImage);
            mUriPosition++;
        }
    }

    private void clearSeenUris() {
        for(int i = 0; i < mUriPosition; i++) {
            mImageUriList.remove(i);
        }
        mUriPosition = 0;
    }


}
