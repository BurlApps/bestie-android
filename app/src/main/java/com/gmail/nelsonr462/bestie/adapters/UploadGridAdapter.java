package com.gmail.nelsonr462.bestie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class UploadGridAdapter extends BaseAdapter {
    private Context mContext;
    private RelativeLayout mGridImageHolder;
    UploadGridAdapter mAdapter = this;

    /* SET CREATE */

    // Keep all Images in array
    public ArrayList<Integer> mThumbIds;


    // Constructor
    public UploadGridAdapter(Context c) {
        mContext = c;
        mThumbIds = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            mThumbIds.add(R.drawable.test_selfie);
        }
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

        holder.gridImage.setImageResource(mThumbIds.get(position));
        mGridImageHolder = (RelativeLayout) convertView.findViewById(R.id.gridImageHolder);
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
