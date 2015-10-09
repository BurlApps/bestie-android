package com.gmail.nelsonr462.bestie.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.events.ImageFlaggedEvent;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class UploadGridAdapter extends BaseAdapter {
    private Context mContext;
    private int mUploadLimit;
    UploadGridAdapter mAdapter = this;
    Uri mPlaceholderUri;


    private ArrayList<ParseObject> mActiveImageList;

    /* SET CREATE */

    // Keep all Images in array
    public ArrayList<String> mImageList = new ArrayList<>();


    // Constructor
    public UploadGridAdapter(Context c, ArrayList<ParseObject> activeImages) {
        mContext = c;
        mActiveImageList = activeImages;
        mPlaceholderUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getResources().getResourcePackageName(R.drawable.add_placeholder_small)
                + '/' + mContext.getResources().getResourceTypeName(R.drawable.add_placeholder_small)
                + '/' + mContext.getResources().getResourceEntryName(R.drawable.add_placeholder_small) );


        if(mActiveImageList.size() > 0) {
            for(int i = 0; i < mActiveImageList.size(); i++) {
                ParseFile image =  mActiveImageList.get(i).getParseFile(ParseConstants.KEY_IMAGE);
                mImageList.add(image.getUrl());
            }
        }

        boolean shared = (boolean) ParseUser.getCurrentUser().get(ParseConstants.KEY_SHARED);
        mUploadLimit = ParseConfig.getCurrentConfig().getInt((!shared)? ParseConstants.KEY_UPLOAD_LIMIT : ParseConstants.KEY_UPLOAD_SHARED_LIMIT);

        Log.d("UPLOAD LIMIT:  ","LIMIT  =  "+mUploadLimit);


        if(mImageList.size() <= mUploadLimit || mImageList.size() == 0) {
            if(mImageList.size() < ParseConfig.getCurrentConfig().getInt(ParseConstants.KEY_UPLOAD_SHARED_LIMIT))
                mImageList.add(mPlaceholderUri.toString());
        }



    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(mImageList.size() > 2) {
            ((Activity) mContext).findViewById(R.id.addPhotosText).setVisibility(View.INVISIBLE);
        } else {
            ((Activity) mContext).findViewById(R.id.addPhotosText).setVisibility(View.VISIBLE);
        }

        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_grid_upload_layout, null);
            holder = new ViewHolder();
            holder.gridImage = (ImageView) convertView.findViewById(R.id.gridImage);
            holder.cancelButton = (ImageView) convertView.findViewById(R.id.delete_button);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mImageList.get(position).equals(mPlaceholderUri.toString())) {
            holder.cancelButton.setVisibility(View.GONE);
            holder.gridImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mImageList.size() < ParseConfig.getCurrentConfig().getInt(ParseConstants.KEY_UPLOAD_LIMIT)+1 || ParseUser.getCurrentUser().get(ParseConstants.KEY_SHARED) == true) {
                        Intent choosePhotoIntent = new Intent (Intent.ACTION_GET_CONTENT);
                        choosePhotoIntent.setType("image/*");
                        ((Activity) mContext).startActivityForResult(choosePhotoIntent, BestieConstants.PICK_PHOTO_REQUEST );
                    } else {
                        new AlertDialog.Builder(mContext).setTitle("Upload limit reached!")
                                .setMessage("Share Bestie to increase your upload limit to 10 photos!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                        sharingIntent.setType("text/plain");
                                        String shareBody = ParseConfig.getCurrentConfig().getString(ParseConstants.KEY_SHARE_MESSAGE);
                                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out Bestie!");
                                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                        ((Activity)mContext).startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), BestieConstants.SHARE_REQUEST);
                                       // Share intent here
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
            });
        }

        Picasso.with(mContext).load(mImageList.get(position)).into(holder.gridImage);
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageList.size() == mUploadLimit && !mImageList.contains(mPlaceholderUri.toString())) mImageList.add(mPlaceholderUri.toString());
                mImageList.remove(position);
                mAdapter.notifyDataSetChanged();

                final ParseObject removedImage = mActiveImageList.get(position);
                ParseObject batch = removedImage.getParseObject(ParseConstants.KEY_BATCH);
                ParseRelation<ParseObject> imageRelation = batch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);
                imageRelation.remove(removedImage);
                batch.increment(ParseConstants.KEY_MAX_VOTES_BATCH, -ParseConfig.getCurrentConfig().getInt(ParseConstants.KEY_IMAGE_MAX_VOTES));
                batch.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        removedImage.deleteInBackground();
                        mActiveImageList.remove(position);
                    }
                });

            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView gridImage;
        ImageView cancelButton;
    }

}
