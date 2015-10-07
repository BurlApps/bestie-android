package com.gmail.nelsonr462.bestie.ui;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.MainFragmentPagerAdapter;
import com.gmail.nelsonr462.bestie.helpers.SlidingTabLayout;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected ParseObject mUserBatch;
    protected ParseUser mCurrentUser;
    private BestieRankFragment mBestieFragment;
    private VoteFragment mVoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
            @Override
            public void done(ParseSession parseSession, ParseException e) {
                if(parseSession == null) {
                    ParseUser.logOut();
                    navigateToLogin();
                }
            }
        });

        mCurrentUser = ParseUser.getCurrentUser();
        if(mCurrentUser == null) {
            navigateToLogin();
            return;
        } else {
            Log.i(TAG, mCurrentUser.getUsername());
            mCurrentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    inflateTabLayout();
//                    mUserBatch = mCurrentUser.getParseObject(ParseConstants.KEY_BATCH);
//
//                    if (mUserBatch != null) {
//                        Log.d(TAG, "USER NAME:  " + mCurrentUser.getUsername());
//
//
//                        mUserBatch.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                            @Override
//                            public void done(ParseObject userBatch, ParseException e) {
//                                if (userBatch != null) {
//                                    Log.d(TAG, "Batch ID:   " + userBatch.getObjectId());
//
//
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(MainActivity.this, "No Active batch!", Toast.LENGTH_SHORT).show();
//                    }

                }
            });

        }


        /* Fetch global config */

        /////

    }

    private void inflateTabLayout() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        MainFragmentPagerAdapter adapter =  new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setCustomTabView(R.layout.tab_icon_layout, 0);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.bestieYellow);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);

        mBestieFragment = (BestieRankFragment) adapter.getItem(2);
        mVoteFragment = (VoteFragment) adapter.getItem(1);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();

            if(BestieRankFragment.mActiveBatchImages.size() > 0)
                ParseQuery.clearAllCachedResults();
            BestieRankFragment.mActiveBatchImages.clear();
            navigateToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == BestieConstants.PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, CropPhotoActivity.class);
            intent.setData(result.getData());
            startActivity(intent);
        }
    }

}
