package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.BestieListAdapter;
import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.helpers.BatchActivator;
import com.gmail.nelsonr462.bestie.helpers.BestieRankHelper;
import com.gmail.nelsonr462.bestie.helpers.GraphDataHelper;
import com.hookedonplay.decoviewlib.DecoView;
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


public class BestieRankFragment extends android.support.v4.app.Fragment {
    private String TAG = BestieRankFragment.class.getSimpleName();

    private GraphDataHelper mGraphDataHelper;
    private BestieRankHelper mBestieRankHelper;
    public static Context mContext;

    private ParseUser mCurrentUser;
    private ParseRelation<ParseObject> mBatchImageRelation;
    public static ParseObject mUserBatch;
    public static ArrayList<ParseObject> mActiveBatchImages = new ArrayList<ParseObject>();;

    private View mView;
    private ListView mRankedPictureList;
    public static GridView mUploadGrid;
    private RelativeLayout mAddPhotosLayout;
    private RelativeLayout mBestieHeader;
    private RelativeLayout mBatchView;
    private DecoView mBatchCompletionGraph;
    private TextView mCompletionPercentage;
    private Button mStartOverButton;
    private Button mShareButton;
    private Button mFindBestieButton;

    private ArrayList<Bitmap> mBitmaps;

    private OnFragmentInteractionListener mListener;

    public BestieRankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_bestie_rank, container, false);
        mBestieHeader = (RelativeLayout) inflater.inflate(R.layout.header_bestie_top_picture, null, false);

        mContext = getActivity();

        /* Set Add Photos Layout */
        mAddPhotosLayout = (RelativeLayout) mView.findViewById(R.id.addPhotosBestieLayout);
        mUploadGrid = (GridView) mView.findViewById(R.id.photoGridView);
        mFindBestieButton = (Button) mView.findViewById(R.id.findNewBestieButton);
        mFindBestieButton.setOnClickListener(ButtonClickListener(3));

        /* Set Batch Completion Graph View */
        mBatchView = (RelativeLayout) mView.findViewById(R.id.batchView);
        mBatchCompletionGraph = (DecoView) mView.findViewById(R.id.batchCompletionGraph);

        /* Set Bestie View */
        mRankedPictureList = (ListView) mView.findViewById(R.id.listView);
        mRankedPictureList.addHeaderView(mBestieHeader);
//        mRankedPictureList.setAdapter(new BestieListAdapter(getActivity()));

        mStartOverButton = (Button) mView.findViewById(R.id.startOverButton);
        mStartOverButton.setOnClickListener(ButtonClickListener(1));
        mShareButton = (Button) mView.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(ButtonClickListener(2));

        /* CONNECT UPLOAD GRID TO PARSE HERE */
        mCurrentUser = ParseUser.getCurrentUser();

        if(mCurrentUser == null) {
            navigateToLogin();
            return mView;
        }

        if(mActiveBatchImages.size() > 0) {
            Toast.makeText(mView.getContext(), "no new pull", Toast.LENGTH_SHORT).show();
            if(mUserBatch != null) {

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
                                            Toast.makeText(mView.getContext(), "Parse Query failed :(", Toast.LENGTH_SHORT).show();
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
                        });
                    } else {
                        Toast.makeText(mView.getContext(), "No Active batch!", Toast.LENGTH_SHORT).show();
                        mAddPhotosLayout.setVisibility(View.VISIBLE);
                        mUploadGrid.setAdapter(new UploadGridAdapter(getActivity(), mActiveBatchImages));
                    }

                }
            });
        }

        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

                } else if(buttonType == 3) {
                    if(mUploadGrid.getAdapter().getCount() < 3) {
                        Toast.makeText(mView.getContext(), "Upload some images first!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Set batch as active and display graph
                        BatchActivator.activateBatch(true);
                        mBatchView.setVisibility(View.VISIBLE);
                        try {
                            VoteFragment.mUserBatch.fetch();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 0, 2100))
                        );
                        set.setDuration(500);
                        set.start();
                    }
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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
