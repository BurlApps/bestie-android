package com.gmail.nelsonr462.bestie.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.greenrobot.event.EventBus;


public class ParseImageHelper {

    private static final String TAG = ParseImageHelper.class.getSimpleName();
    private List<com.makeramen.roundedimageview.RoundedImageView> mImageViewList;
    private ArrayList<Uri> mImageUriList = new ArrayList<>();
    private ArrayList<TextView> mPercentViews;
    private Context mContext;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mCheckNowLayout;
    private RelativeLayout mRootLayout;
    private int mNextPair;
    private ArrayList<ParseObject> mParseImageObjects = new ArrayList<>();
    protected int mUriPosition;
    private boolean mMinVotesReached;

    public ParseImageHelper(List<com.makeramen.roundedimageview.RoundedImageView> imageViewList,
                            Context context, RelativeLayout loadingLayout, RelativeLayout checkNowLayout,
                            RelativeLayout rootLayout, ArrayList<TextView> percentViews){
        mImageViewList = imageViewList;
        mPercentViews = percentViews;
        mContext = context;
        mLoadingLayout = loadingLayout;
        mUriPosition = 0;
        mCheckNowLayout = checkNowLayout;
        mRootLayout = rootLayout;
    }


    public void pullVoteImages(final int pullType){
        mRootLayout.setEnabled(false);
        if (pullType == 3) {
            mParseImageObjects.clear();
            mImageUriList.clear();
            mUriPosition = 0;
        }

        final HashMap<String, Object> params = new HashMap<String, Object>();

        ParseCloud.callFunctionInBackground(ParseConstants.FEED, params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> list, ParseException e) {
                mRootLayout.setEnabled(true);
                if (e != null) {
                    Log.d(TAG, e.getMessage());
                    return;
                }

                if(list.size() < 4) {
                    mCheckNowLayout.setVisibility(View.VISIBLE);
                    for (int j = 0; j < mImageUriList.size(); j++) {
                        Log.d(TAG, "IMAGE ID:  "+mImageUriList.get(j)+"  /  OBJECT ID:  "+mParseImageObjects.get(j).getObjectId());

                    }
                    mRootLayout.setEnabled(false);
                    return;
                }

                for (int i = 0; i < list.size(); i++) {
//                    if(!mParseImageObjects.contains(list.get(i))) {
                        ParseFile image = list.get(i).getParseFile(ParseConstants.KEY_IMAGE);
                        Uri imageUri = Uri.parse(image.getUrl());
                        mImageUriList.add(imageUri);
                        mParseImageObjects.add(list.get(i));
//                    }
                }
                Log.d(TAG, "IMAGE URI LIST SIZE:   " + mImageUriList.size());
                for (int j = 0; j < mImageUriList.size(); j++) {
                    Log.d(TAG, "IMAGE ID:  "+mImageUriList.get(j)+"  /  OBJECT ID:  "+mParseImageObjects.get(j).getObjectId());

                }
                if (pullType == 0 || pullType == 3) {
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
            float percent = ((float) mParseImageObjects.get(i).getInt(ParseConstants.KEY_WINS) / (float) mParseImageObjects.get(i).getInt(ParseConstants.KEY_VOTES))*100;
            mPercentViews.get(i).setText((int) percent + "%");
            mUriPosition++;
            mLoadingLayout.setVisibility(View.INVISIBLE);
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
            float percent = ((float) mParseImageObjects.get(mUriPosition).getInt(ParseConstants.KEY_WINS) / (float) mParseImageObjects.get(mUriPosition).getInt(ParseConstants.KEY_VOTES))*100;
            mPercentViews.get((nextPair == 0) ? 0 : 2).setText((int) percent + "%");
            mUriPosition++;
        }
        if (mUriPosition < mImageUriList.size()) {
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).into(mImageViewList.get((nextPair == 0) ? 1 : 3));
            float percent = ((float) mParseImageObjects.get(mUriPosition).getInt(ParseConstants.KEY_WINS) / (float) mParseImageObjects.get(mUriPosition).getInt(ParseConstants.KEY_VOTES))*100;
            mPercentViews.get((nextPair == 0) ? 1 : 3).setText((int) percent + "%");
            mUriPosition++;
        }

        mNextPair = (mNextPair == 0)? 1 : 0;
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

                    if(o != null) {
                        Log.d(TAG, "RETURNED:   " + o.toString());
                        EventBus.getDefault().post(new ImageVotedEvent(o));
                    }
                }
            });

        }
    }

    public void flagImage(int imagePosition) {
        ParseObject flaggedImage = mParseImageObjects.get(imagePosition);
        Log.d(TAG, "FLAGGED STATUS:   "+flaggedImage.getBoolean(ParseConstants.KEY_FLAGGED));
//        flaggedImage.put(ParseConstants.KEY_FLAGGED, true);
//        flaggedImage.saveInBackground();
    }

}
