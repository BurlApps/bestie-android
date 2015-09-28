package com.gmail.nelsonr462.bestie.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.MainActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class UploadGridAdapter extends BaseAdapter {
    private Context mContext;
    UploadGridAdapter mAdapter = this;
    Uri mPlaceholderUri;


    private List<ParseObject> mActiveImageList;

    /* SET CREATE */

    // Keep all Images in array
    public static ArrayList<Integer> mThumbIds;
    public static ArrayList<Uri> mImageList;


    // Constructor
    public UploadGridAdapter(Context c, List<ParseObject> activeImages) {
        mContext = c;
        mActiveImageList = activeImages;




        for(int i = 0; i < mActiveImageList.size(); i++) {
            ParseFile image =  mActiveImageList.get(i).getParseFile(ParseConstants.KEY_IMAGE);
            Uri imageUri = Uri.parse(image.getUrl());
            mImageList.add(imageUri);
        }

        if(mImageList.size() < 5) {
            mImageList.add(mPlaceholderUri);
        }


        mThumbIds = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            mThumbIds.add(R.drawable.test_selfie);
            if(i == 3) {
                mThumbIds.add(R.drawable.add_placeholder);
            }
        }
    }

    public UploadGridAdapter(Context c) {
        mContext = c;

        mThumbIds = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            mThumbIds.add(R.drawable.test_selfie);
            if(i == 3) {
                mThumbIds.add(R.drawable.add_placeholder);
            }
        }

        mPlaceholderUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getResources().getResourcePackageName(R.drawable.add_placeholder)
                + '/' + mContext.getResources().getResourceTypeName(R.drawable.add_placeholder)
                + '/' + mContext.getResources().getResourceEntryName(R.drawable.add_placeholder) );
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

//        if(mImageList.get(position).equals(mPlaceholderUri)) {
//            holder.cancelButton.setVisibility(View.GONE);
//            holder.gridImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent choosePhotoIntent = new Intent (Intent.ACTION_GET_CONTENT);
//                    choosePhotoIntent.setType("image/*");
//                    ((Activity) mContext).startActivityForResult(choosePhotoIntent, BestieConstants.PICK_PHOTO_REQUEST );
//                }
//            });
//        }

        if(position == mThumbIds.size() - 1) {
            holder.cancelButton.setVisibility(View.GONE);
            holder.gridImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent choosePhotoIntent = new Intent (Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    ((Activity) mContext).startActivityForResult(choosePhotoIntent, BestieConstants.PICK_PHOTO_REQUEST );
                }
            });
        }

        holder.gridImage.setImageResource(mThumbIds.get(position));
//        holder.gridImage.setImageURI(mImageList.get(position));
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mThumbIds.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView gridImage;
        ImageView cancelButton;
    }
}
