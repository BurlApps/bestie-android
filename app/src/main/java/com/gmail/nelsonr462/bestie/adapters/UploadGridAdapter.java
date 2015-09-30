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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class UploadGridAdapter extends BaseAdapter {
    private Context mContext;
    UploadGridAdapter mAdapter = this;
    Uri mPlaceholderUri;


    private ArrayList<ParseObject> mActiveImageList;

    /* SET CREATE */

    // Keep all Images in array
    public static ArrayList<String> mImageList = new ArrayList<>();


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

        if(mImageList.size() < 10) {
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
                    Intent choosePhotoIntent = new Intent (Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    ((Activity) mContext).startActivityForResult(choosePhotoIntent, BestieConstants.PICK_PHOTO_REQUEST );
                }
            });
        }

        Picasso.with(mContext).load(mImageList.get(position)).into(holder.gridImage);
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mImageList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView gridImage;
        ImageView cancelButton;
    }


    public void newImage(){

    }
}
