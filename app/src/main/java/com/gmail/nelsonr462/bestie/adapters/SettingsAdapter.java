package com.gmail.nelsonr462.bestie.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.gmail.nelsonr462.bestie.ui.LegalViewActivity;
import com.gmail.nelsonr462.bestie.ui.MainActivity;
import com.gmail.nelsonr462.bestie.ui.VoteFragment;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;


public class SettingsAdapter extends BaseAdapter{
    private String TAG = SettingsAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> mSettingsOptions = new ArrayList<>();
    private int mSection;
    private String[][] mListOptions = {{"Rate The App", "Share Bestie"}, {"Show Me", "Logout & Erase All Data"}, {"Terms of Service", "Privacy Policy"}};

    public SettingsAdapter(Context context, int section) {
        mContext = context;
        mSection = section;

        for (int i = 0; i < mListOptions[mSection].length; i++) {
            mSettingsOptions.add( mListOptions[mSection][i]);
        }
    }

    @Override
    public int getCount() {
        return mSettingsOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mSettingsOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_settings_list, null);
            holder = new ViewHolder();

            holder.settingsOption = (TextView) convertView.findViewById(R.id.settingsOption);
            holder.settingsDescription = (TextView) convertView.findViewById(R.id.settingsDescription);
            holder.settingsMore = (ImageView) convertView.findViewById(R.id.settingsMore);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.settingsOption.setText(mSettingsOptions.get(position));

        if(mSection == 1) {
            if(position == 0) {
                String interested = (ParseUser.getCurrentUser().getString(ParseConstants.KEY_INTERESTED).equals(ParseConstants.STRING_MALE)) ? "Men" : "Women";
                holder.settingsMore.setVisibility(View.INVISIBLE);
                holder.settingsDescription.setText(interested);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setItems(R.array.interested_choices, interestedDialogListener(holder));
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }

            if(position == 1) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseUser.logOut();

                        if(BestieRankFragment.mActiveBatchImages.size() > 0)
                            ParseQuery.clearAllCachedResults();
                        BestieRankFragment.mActiveBatchImages.clear();
                        ((MainActivity) mContext).navigateToLogin();
                    }
                });
            }
        }


        if(mSection == 2) {
            final String url = (position == 0)? ParseConfig.getCurrentConfig().getString("termsURL") : ParseConfig.getCurrentConfig().getString("privacyURL");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LegalViewActivity.class);
                    intent.putExtra("legalUrl", url);
                    mContext.startActivity(intent);
                }
            });

        }



        return convertView;
    }


    private static class ViewHolder {
        TextView settingsOption;
        TextView settingsDescription;
        ImageView settingsMore;
    }

    private DialogInterface.OnClickListener interestedDialogListener(final ViewHolder holder) {
        return new DialogInterface.OnClickListener() {
           ParseUser currentUser = ParseUser.getCurrentUser();
           @Override
           public void onClick(DialogInterface dialog, int which) {
               switch (which) {
                   case 0:
                       currentUser.put(ParseConstants.KEY_INTERESTED, "male");
                       break;
                   case 1:
                       currentUser.put(ParseConstants.KEY_INTERESTED, "female");
                       break;
               }

               currentUser.saveInBackground(new SaveCallback() {
                   @Override
                   public void done(ParseException e) {
                       holder.settingsDescription.setText(currentUser.getString(ParseConstants.KEY_INTERESTED));
                       notifyDataSetChanged();
                       VoteFragment voteFragment = (VoteFragment) ((MainActivity) mContext)
                               .getSupportFragmentManager()
                               .findFragmentByTag("android:switcher:" + R.id.pager +":1");
                       voteFragment.newPull();
                   }
               });

           }
       };
    }

}
