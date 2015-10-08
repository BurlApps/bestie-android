package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.BestieListAdapter;
import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.helpers.BatchActivator;
import com.gmail.nelsonr462.bestie.events.BatchUpdateEvent;
import com.gmail.nelsonr462.bestie.helpers.GraphDataHelper;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class BestieRankFragment extends android.support.v4.app.Fragment {
    private final String TAG = BestieRankFragment.class.getSimpleName();

    private GraphDataHelper mGraphDataHelper;
    public static Context mContext;

    private ParseUser mCurrentUser;
    private ParseRelation<ParseObject> mBatchImageRelation;
    public static ParseObject mUserBatch;
    public static ArrayList<ParseObject> mActiveBatchImages = new ArrayList<ParseObject>();;

    private View mView;
    private ViewPager mViewPager;
    private ListView mRankedPictureList;
    public static GridView mUploadGrid;
    private RelativeLayout mAddPhotosLayout;
    private RelativeLayout mBestieHeader;
    private RelativeLayout mBatchView;
    private Button mStartOverButton;
    private Button mShareButton;
    private Button mFindBestieButton;
    private Button mContinueButton;

    public BestieRankFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_bestie_rank, container, false);
        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mBestieHeader = (RelativeLayout) inflater.inflate(R.layout.header_bestie_top_picture, null, false);

        mContext = getActivity();

        /* Set Add Photos Layout */
        mAddPhotosLayout = (RelativeLayout) mView.findViewById(R.id.addPhotosBestieLayout);
        mUploadGrid = (GridView) mView.findViewById(R.id.photoGridView);
        mFindBestieButton = (Button) mView.findViewById(R.id.findNewBestieButton);
        mFindBestieButton.setOnClickListener(ButtonClickListener(3));

        /* Set Batch Completion Graph View */
        mBatchView = (RelativeLayout) mView.findViewById(R.id.batchView);

        /* Set Bestie View */
        mRankedPictureList = (ListView) mView.findViewById(R.id.listView);
        mRankedPictureList.addHeaderView(mBestieHeader);

        mStartOverButton = (Button) mView.findViewById(R.id.startOverButton);
        mStartOverButton.setOnClickListener(ButtonClickListener(1));
        mShareButton = (Button) mView.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(ButtonClickListener(2));
        mContinueButton = (Button) mView.findViewById(R.id.continueVotingButton);
        mContinueButton.setOnClickListener(ButtonClickListener(4));

        /* CONNECT UPLOAD GRID TO PARSE HERE */
        mCurrentUser = ParseUser.getCurrentUser();


        if(mCurrentUser == null) {
//            navigateToLogin();
            return mView;
        }

        if(mActiveBatchImages.size() > 0) {
            checkForBatch();
        } else {

            mCurrentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {


                    if (e != null) {
                        Log.d(TAG, "Get current user failed");
                        return;
                    }

                    mUserBatch = mCurrentUser.getParseObject(ParseConstants.KEY_BATCH);

                    if (mUserBatch != null) {
                        Log.d(TAG, "USER NAME:  " + mCurrentUser.getUsername());


                        mUserBatch.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject userBatch, ParseException e) {
                                if (userBatch != null) {
                                    Log.d(TAG, "Batch ID:   " + userBatch.getObjectId());


                                    if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == true) {
                                        mBatchView.setVisibility(View.VISIBLE);
                                        mGraphDataHelper = new GraphDataHelper(mBatchView);
                                    }

                                    mBatchImageRelation = mUserBatch.getRelation(ParseConstants.KEY_BATCH_IMAGE_RELATION);

                                    ParseQuery<ParseObject> query = mBatchImageRelation.getQuery();
                                    query.addAscendingOrder((mUserBatch.get(ParseConstants.KEY_VOTES) == 0) ? ParseConstants.KEY_CREATED_AT : ParseConstants.KEY_SCORE);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> list, ParseException e) {
                                            if (e != null) {
                                                return;
                                            }

                                            if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == false && mUserBatch.getInt(ParseConstants.KEY_VOTES) == 0) {
                                                mAddPhotosLayout.setVisibility(View.VISIBLE);

                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                mActiveBatchImages.add(list.get(i));
                                            }

                                            if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == false && mUserBatch.getInt(ParseConstants.KEY_VOTES) > 0) {
                                                RoundedImageView theBestie = (RoundedImageView) mBestieHeader.findViewById(R.id.theBestiePic);
                                                Picasso.with(getActivity()).load(mActiveBatchImages.get(0).getParseFile(ParseConstants.KEY_IMAGE).getUrl()).into(theBestie);
                                                ArrayList<ParseObject> rankedImages = new ArrayList<ParseObject>();
                                                for (int i = 1; i < mActiveBatchImages.size(); i++) {
                                                    rankedImages.add(mActiveBatchImages.get(i));
                                                }


                                                mRankedPictureList.setAdapter(new BestieListAdapter(mContext, rankedImages));
                                            }

                                            mUploadGrid.setAdapter(new UploadGridAdapter(getActivity(), mActiveBatchImages));
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        mAddPhotosLayout.setVisibility(View.VISIBLE);
                        mUploadGrid.setAdapter(new UploadGridAdapter(getActivity(), mActiveBatchImages));
                    }

                }
            });
        }


        final Handler h = new Handler();
        final int delay = 4000; //milliseconds


        h.postDelayed(new Runnable() {
            public void run() {
                if (mUserBatch != null) {
                    mUserBatch.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject userBatch, ParseException e) {
                            if (e != null) {
                                Log.d(TAG, e.getMessage());
                            }

                                if (userBatch != null) {

                                    if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == false && mUserBatch.getInt(ParseConstants.KEY_VOTES) > 0 && mActiveBatchImages.size() > 0 && mBatchView.getVisibility() == View.VISIBLE) {
                                        RoundedImageView theBestie = (RoundedImageView) mBestieHeader.findViewById(R.id.theBestiePic);
                                        Picasso.with(getActivity()).load(mActiveBatchImages.get(0).getParseFile(ParseConstants.KEY_IMAGE).getUrl()).into(theBestie);
                                        ArrayList<ParseObject> rankedImages = new ArrayList<ParseObject>();
                                        for (int i = 1; i < mActiveBatchImages.size(); i++) {
                                            rankedImages.add(mActiveBatchImages.get(i));
                                        }
                                        mRankedPictureList.setAdapter(new BestieListAdapter(mContext, rankedImages));

                                    }

                                    EventBus.getDefault().post(new BatchUpdateEvent(userBatch));

                                    if(mGraphDataHelper != null) mGraphDataHelper.updateGraph();
                            }
                        }
                    });
                }
                h.postDelayed(this, delay);
            }
        }, delay);


        return mView;
    }

    private void checkForBatch() {
        if(mUserBatch != null) {
            if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == true) {
                mBatchView.setVisibility(View.VISIBLE);
                mGraphDataHelper = new GraphDataHelper(mBatchView);
            }

            if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == false && mUserBatch.getInt(ParseConstants.KEY_VOTES) == 0) {
                mAddPhotosLayout.setVisibility(View.VISIBLE);

            }

            if (mUserBatch.get(ParseConstants.KEY_ACTIVE) == false && mUserBatch.getInt(ParseConstants.KEY_VOTES) > 0) {
                RoundedImageView theBestie = (RoundedImageView) mBestieHeader.findViewById(R.id.theBestiePic);
                Picasso.with(getActivity()).load(mActiveBatchImages.get(0).getParseFile(ParseConstants.KEY_IMAGE).getUrl()).into(theBestie);
                ArrayList<ParseObject> rankedImages = new ArrayList<ParseObject>();
                for (int i = 1; i < mActiveBatchImages.size(); i++) {
                    rankedImages.add(mActiveBatchImages.get(i));
                }


                mRankedPictureList.setAdapter(new BestieListAdapter(mContext, rankedImages));
            }

            mUploadGrid.setAdapter(new UploadGridAdapter(getActivity(), mActiveBatchImages));
        } else {
            mAddPhotosLayout.setVisibility(View.VISIBLE);
            mUploadGrid.setAdapter(new UploadGridAdapter(getActivity(), mActiveBatchImages));
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View.OnClickListener ButtonClickListener(final int buttonType) {
        View.OnClickListener onClickListener;

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout addPhotosLayout = (RelativeLayout) mView.findViewById(R.id.addPhotosBestieLayout);

                if(buttonType == 1) {
                    // Reset mActiveBatchImages and set new GridLayoutAdapter
                    // Remove current batch from user batch pointer field

                    mActiveBatchImages.clear();
                    mUploadGrid.setAdapter(new UploadGridAdapter(mContext, mActiveBatchImages));
                    BatchActivator.moveBatch();

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 2100, 0))
                    );
                    set.setDuration(500);
                    addPhotosLayout.setVisibility(View.VISIBLE);
                    set.start();

                } else if (buttonType == 2) {
                    // Save button

                } else if(buttonType == 3) {
                    if(mUploadGrid.getAdapter().getCount() < 3) {
                        Snackbar.make(v, "Upload some images first!", Snackbar.LENGTH_LONG).show();
                    } else {
                        // Set batch as active and display graph
                        BatchActivator.activateBatch(true);
                        mGraphDataHelper = new GraphDataHelper(mBatchView);
                        mBatchView.setVisibility(View.VISIBLE);

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 0, 2100))
                        );
                        set.setDuration(500);
                        set.start();
                    }
                } else if (buttonType == 4) {
                    // Continue voting button
                    mViewPager.setCurrentItem(1, true);

                }
            }
        };
        return onClickListener;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(mView.getContext(), WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void setUserBatch(ParseObject batch) {
        mUserBatch = batch;
    }

    public ParseObject getUserBatch() {
        return mUserBatch;
    }



}
