package com.gmail.nelsonr462.bestie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.R;

import java.util.ArrayList;


public class SettingsAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> mSettingsOptions;

    public SettingsAdapter(Context context, int section) {
        mContext = context;


    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_settings_list, null);
            holder = new ViewHolder();

            holder.settingsOption = (TextView) convertView.findViewById(R.id.settingsOption);
            holder.settingsDescription = (TextView) convertView.findViewById(R.id.settingsDescription);
            holder.settingsMore = (ImageView) convertView.findViewById(R.id.settingsMore);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        return convertView;
    }


    private static class ViewHolder {
        TextView settingsOption;
        TextView settingsDescription;
        ImageView settingsMore;
    }
}
