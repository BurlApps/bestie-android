package com.gmail.nelsonr462.bestie.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.gmail.nelsonr462.bestie.ui.VoteFragment;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


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

        final HashMap<String, Object> params = new HashMap<String, Object>();

        ParseCloud.callFunctionInBackground(ParseConstants.FEED, params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> list, ParseException e) {
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
                    mRootLayout.setEnabled(false);
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

            final HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("winner", selectedImage.getObjectId());
            params.put("loser", losingImage.getObjectId());

            ParseCloud.callFunctionInBackground(ParseConstants.SET_VOTED, params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if(e!=null)
                        Log.d(TAG, e.getMessage());
                    ParseObject updatedBatch = (ParseObject) o;

                    if(o != null) {
                        Log.d(TAG, "RETURNED ID:   " + updatedBatch.getObjectId());
                        VoteFragment.mUserBatch = (ParseObject) o;
                    }
                }
            });

        }
    }

}
