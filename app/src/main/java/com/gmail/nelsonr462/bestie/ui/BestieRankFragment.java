package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
import android.widget.TextView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.BestieListAdapter;
import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.helpers.BatchActivator;
import com.gmail.nelsonr462.bestie.events.BatchUpdateEvent;
import com.gmail.nelsonr462.bestie.helpers.GraphDataHelper;
import com.gmail.nelsonr462.bestie.helpers.ParseImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

    private Vibrator mVibrator;

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
    public RelativeLayout mAddPhotosLayout;
    private RelativeLayout mBestieHeader;
    private RelativeLayout mBatchView;
    private Button mStartOverButton;
    private Button mShareButton;
    private Button mFindBestieButton;
    private Button mContinueButton;

    private final String mFormat =  "%.0f%%";


    private final Handler h = new Handler();
    private final int delay = 4000; //milliseconds

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

        mVibrator = (Vibrator) mView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

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
            return mView;
        }

//        if(mActiveBatchImages.size() > 0) {
//            checkForBatch();
//        } else {

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
                                    if (mUserBatch.get(ParseConstants.KEY_VOTES) == 0) {
                                        query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
                                    } else {
                                        query.addDescendingOrder(ParseConstants.KEY_SCORE);
                                    }
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> list, ParseException e) {

                                            /*   ADD IN PROGRESS BAR HERE   */

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
                                                TextView bestiePercent = (TextView) mBestieHeader.findViewById(R.id.bestiePercent);
                                                setPercent(bestiePercent);
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
//        }


        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        h.postDelayed(r, delay);
    }

    @Override
    public void onPause() {
        h.removeCallbacks(r);
        super.onPause();
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
                    ParseFile imageToSave = mActiveBatchImages.get(0).getParseFile(ParseConstants.KEY_IMAGE);
                    ParseImageHelper.saveImage(imageToSave);
                    mVibrator.vibrate(100);

                } else if(buttonType == 3) {
                    if(mUploadGrid.getAdapter().getCount() < 3) {
                        // Check for valid upload count
                        final Snackbar snackbar = Snackbar.make(mView, "Upload some images first!", 5000);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(getResources().getColor(R.color.bestieBlue));

                        snackbar.setAction("Okay", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.bestieYellow)).show();
                        mVibrator.vibrate(300);


                    } else {
                        // Set batch as active and display graph
                        MainActivity activity = (MainActivity) getActivity();
                        BatchActivator.activateBatch(true);
                        mGraphDataHelper = new GraphDataHelper(mBatchView);
                        mBatchView.setVisibility(View.VISIBLE);

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 0, 2100))
                        );
                        set.setDuration(500);
                        set.start();

                        // Onboarding activation
                        if(BestieConstants.UPLOAD_ONBOARDING_ACTIVE) {
                            BestieConstants.FROM_FIRST_UPLOAD = true;
                            activity.mViewPager.setCurrentItem(1);
                        }
                    }
                } else if (buttonType == 4) {
                    // Continue voting button
                    mViewPager.setCurrentItem(1, true);

                }
            }
        };
        return onClickListener;
    }


    private void setPercent(TextView textView) {
        float percent;
//        if( mActiveBatchImages.get(0).getInt(ParseConstants.KEY_VOTES) > 0) {
            percent =  (float) ((double) mActiveBatchImages.get(0).getNumber("percent") * 100);
//        } else {
//            percent =  (float) ((int) mActiveBatchImages.get(0).getNumber("percent") * 100);
//        }
        textView.setText(String.format(mFormat, percent));
    }

    private Runnable r = new Runnable() {
        public void run() {
            Log.d(TAG, "runnable active");
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
                                TextView bestiePercent = (TextView) mBestieHeader.findViewById(R.id.bestiePercent);
                                setPercent(bestiePercent);
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
    };
}
