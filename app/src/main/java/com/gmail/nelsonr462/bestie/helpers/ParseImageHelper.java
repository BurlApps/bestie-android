package com.gmail.nelsonr462.bestie.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.BestieApplication;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.events.ImageFlaggedEvent;
import com.gmail.nelsonr462.bestie.events.ImageVotedEvent;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    private final String mFormat =  "%.0f%%";

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
                    ParseFile image = list.get(i).getParseFile(ParseConstants.KEY_IMAGE);
                    Uri imageUri = Uri.parse(image.getUrl());
                    mImageUriList.add(imageUri);
                    mParseImageObjects.add(list.get(i));
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
            Picasso.with(mContext).load(mImageUriList.get(i)).placeholder(R.drawable.placeholder).into(mImageViewList.get(i));
            setPercent(mPercentViews.get(i), mUriPosition);
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
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).placeholder(R.drawable.placeholder).into(mImageViewList.get((nextPair == 0) ? 0 : 2));
            setPercent(mPercentViews.get((nextPair == 0) ? 0 : 2), mUriPosition);
            mUriPosition++;
        }
        if (mUriPosition < mImageUriList.size()) {
            Picasso.with(mContext).load(mImageUriList.get(mUriPosition)).placeholder(R.drawable.placeholder).into(mImageViewList.get((nextPair == 0) ? 1 : 3));
            setPercent(mPercentViews.get((nextPair == 0) ? 1 : 3), mUriPosition);
            mUriPosition++;
        }

        mNextPair = (mNextPair == 0)? 1 : 0;
    }

    public void setImageVoted(final int imagePosition) {
        Log.d(TAG, "WIN POSITION:  "+imagePosition);
        Log.d(TAG, "LIST SIZE:   "+mParseImageObjects.size());
        // Increment votes, wins/losses
        ParseObject selectedImage;
        ParseObject losingImage;

        if (imagePosition + 1 < mParseImageObjects.size()) {
            selectedImage = mParseImageObjects.get(imagePosition);
            losingImage = mParseImageObjects.get((imagePosition % 2 == 0) ? imagePosition + 1 : imagePosition - 1);

            final HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("winner", selectedImage.getObjectId());
            params.put("loser", losingImage.getObjectId());

            ParseCloud.callFunctionInBackground(ParseConstants.SET_VOTED, params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {

                    JSONObject props = new JSONObject();
                    try {
                        props.put("Position", (imagePosition%2 == 0)? "Top" : "Bottom");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    BestieApplication.mMixpanel.track("Mobile.Set.Voted", props);
                    Log.d(TAG, "RETURNED:  IMAGES VOTED ");
                    if (e != null)
                        Log.d(TAG, e.getMessage());

                    if (o != null) {
                        BestieApplication.mMixpanel.getPeople().increment("Votes", 1);
                        if(BestieRankFragment.mUserBatch.getBoolean(ParseConstants.KEY_ACTIVE)) {
                            EventBus.getDefault().post(new ImageVotedEvent(o));
                        }
                    }
                }
            });

        }

    }

    public void flagImage(int imagePosition, final View view) {
        final ParseObject flaggedImage = mParseImageObjects.get(imagePosition);
        new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.flag_picture_title))
                .setMessage(mContext.getString(R.string.flag_picture_message))
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(view, "Flagged picture", Snackbar.LENGTH_LONG).show();
                        flaggedImage.put(ParseConstants.KEY_FLAGGED, true);
                        flaggedImage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                EventBus.getDefault().post(new ImageFlaggedEvent());
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void saveImage(ParseFile image) {
        if (isExternalStorageAvailable()) {
            // Get URI
            // Get external storage directory
            File mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Bestie");
            // Create subdirectory
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory");
                    return;
                }
            }
            // Create a file name
            // Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
            String path = mediaStorageDir.getPath() + File.separator;
            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            AlertDialog.Builder alert = new AlertDialog.Builder(BestieRankFragment.mContext).setTitle("Save Error")
                    .setMessage("There was an error saving the file")
                    .setPositiveButton("Okay", null);

            try {
                byte[] file = image.getData();
                FileOutputStream outputStream = new FileOutputStream(mediaFile);
                outputStream.write(file);
                outputStream.close();
            } catch (ParseException e) {
                alert.show();
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                alert.show();
            } catch (IOException e) {
                alert.show();
                e.printStackTrace();
            }

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(mediaFile));
            BestieRankFragment.mContext.sendBroadcast(mediaScanIntent);

            new AlertDialog.Builder(BestieRankFragment.mContext).setTitle("Saved Bestie")
                    .setMessage("Image saved successfully!")
                    .setPositiveButton("Okay", null)
                    .show();

            BestieApplication.mMixpanel.track("Mobile.User.Save");

        }
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void setPercent(TextView textView, int position) {
        float percent;
        String zeroCheck = mParseImageObjects.get(position).get("percent") + "";
        if (zeroCheck.equals("0")) {
            percent = 0;
        } else if(zeroCheck.equals("1")){
            percent = 100.0f;
        } else {
            percent = (float) ((double) mParseImageObjects.get(position).getNumber("percent") * 100);
        }

        textView.setText(String.format(mFormat, percent));
    }

}
