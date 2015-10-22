package com.gmail.nelsonr462.bestie.adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.BestieApplication;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.gmail.nelsonr462.bestie.ui.LegalViewActivity;
import com.gmail.nelsonr462.bestie.ui.MainActivity;
import com.gmail.nelsonr462.bestie.ui.VoteFragment;
import com.parse.ParseConfig;
import com.parse.ParseException;
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

        if (mSection == 0) {
            if (position == 0) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            mContext.startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                        }
                    }
                });
            }


            if (position == 1) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = ParseConfig.getCurrentConfig().getString(ParseConstants.KEY_SHARE_MESSAGE);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out Bestie!");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));

                        BestieApplication.mMixpanel.getPeople().set("Shared", true);
                    }
                });
            }
        }

        if(mSection == 1) {
            if(position == 0) {
                String userKey = ParseUser.getCurrentUser().getString(ParseConstants.KEY_INTERESTED);
                String interested = "";
                switch (userKey) {
                    case "male":
                        interested = "Men";
                        break;
                    case "female":
                        interested = "Women";
                        break;
                    case "both":
                        interested = "Both";
                        break;
                }


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
                    public void onClick(final View v) {
                        new android.support.v7.app.AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.delete_account_title))
                                .setMessage(mContext.getString(R.string.delete_confirmation_message))
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ParseUser.logOut();
                                        BestieApplication.mMixpanel.track("Mobile.User.Logout");
                                        BestieApplication.mMixpanel.flush();
                                        BestieRankFragment.mActiveBatchImages.clear();
                                        ((MainActivity) mContext).navigateToLogin();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });
            }
        }


        if(mSection == 2) {
            final String url = (position == 0)? ParseConfig.getCurrentConfig()
                    .getString(ParseConstants.KEY_TERMS_URL) : ParseConfig.getCurrentConfig().getString(ParseConstants.KEY_PRIVACY_URL);
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
                   case 2:
                       currentUser.put(ParseConstants.KEY_INTERESTED, "both");
                       break;
               }

               currentUser.saveInBackground(new SaveCallback() {
                   @Override
                   public void done(ParseException e) {
                       BestieApplication.mMixpanel.getPeople().set("Interested", currentUser.get(ParseConstants.KEY_INTERESTED));
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
