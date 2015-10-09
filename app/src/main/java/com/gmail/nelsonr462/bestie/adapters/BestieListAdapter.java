package com.gmail.nelsonr462.bestie.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BestieListAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<ParseObject> mActiveImageList;

    /* SET CREATE */

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.test_selfie, R.drawable.test_selfie,
            R.drawable.test_selfie, R.drawable.test_selfie,
            R.drawable.test_selfie,
    };

    // Constructor
    public BestieListAdapter(Context context, ArrayList<ParseObject> activeImageList) {
        mContext = context;
        mActiveImageList = activeImageList;
    }





    public BestieListAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mActiveImageList.size();  /*mThumbIds.length;*/
    }

    @Override
    public Object getItem(int position) {
        return mActiveImageList.get(position);  /*mThumbIds[position];*/
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
            holder.rankPercent = (TextView) convertView.findViewById(R.id.rankPercent);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext).load(mActiveImageList.get(position).getParseFile(ParseConstants.KEY_IMAGE).getUrl()).into(holder.rankImage);
        float percent = ((float) mActiveImageList.get(position).getInt(ParseConstants.KEY_WINS) / (float) mActiveImageList.get(position).getInt(ParseConstants.KEY_VOTES))*100;
        holder.rankPercent.setText((int) percent + "%");


//        holder.rankImage.setImageResource(mThumbIds[position]);
        return convertView;
    }

    private static class ViewHolder {
        ImageView rankImage;
        TextView rankPercent;
    }


}
