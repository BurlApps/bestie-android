package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelson on 9/27/15.
 */
public class ParseImagePuller {

    private static final String TAG = ParseImagePuller.class.getSimpleName();
    private List<com.makeramen.roundedimageview.RoundedImageView> mImageViewList;
    private ArrayList<Uri> mImageUriList = new ArrayList<>();
    private Context mContext;

    public ParseImagePuller(List<com.makeramen.roundedimageview.RoundedImageView> imageViewList, Context context){
        mImageViewList = imageViewList;
        mContext = context;
    }


    public void pullVoteImages(){


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
                Log.d(TAG, "IMAGE URI LIST SIZE:   "+mImageUriList.size());
                loadImagesIntoViews();

            }
        });

         /* Image from parse */

        // pull 20
        // refresh at 4 sets remaining
        // check if user's own picture     x
        // check if active      x
        // check if they haven't voted on it
        // check if gender is included in interested    x
        // add ascending by object id    x

    }

    public void loadImagesIntoViews() {
        for(int i = 0; i < mImageViewList.size(); i++) {
            Picasso.with(mContext).load(mImageUriList.get(i)).into(mImageViewList.get(i));
//            mImageViewList.get(i).setImageURI(mImageUriList.get(i));
        }
    }


}
