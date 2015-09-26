package com.gmail.nelsonr462.bestie.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gmail.nelsonr462.bestie.R;

public class BestieListAdapter extends BaseAdapter{
    private Context mContext;

    /* SET CREATE */

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.test_selfie, R.drawable.test_selfie,
            R.drawable.test_selfie, R.drawable.test_selfie,
            R.drawable.test_selfie,
    };

    // Constructor
    public BestieListAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_bestie_layout, null);
            holder = new ViewHolder();
            holder.rankImage = (ImageView) convertView.findViewById(R.id.bestieRankListImage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.rankImage.setImageResource(mThumbIds[position]);
        return convertView;
    }

    private static class ViewHolder {
        ImageView rankImage;
    }


}
