package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.helpers.ParseImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class VoteFragment extends android.support.v4.app.Fragment {
    private String TAG = VoteFragment.class.getSimpleName();

    public static ParseObject mUserBatch;

    private RelativeLayout mRootLayout;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mCheckNowLayout;
    private List<RelativeLayout> mVotingImages = new ArrayList<>();
    private ImageView mVoteCounter;
    private View mView;
    private Button mCheckNowButton;
    private ParseImageHelper mImagePuller;
    private int mPairSwitch;
    private int mTopImagePosition;
    private int mBottomImagePosition;

    private float mScreenHeight;
    private float mCounterPosition;
    private float mIncrement;
    private float mVotesNeeded;
    private float mVoteCount;

    Rect outRect = new Rect();
    int[] location = new int[2];

    public VoteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_vote, container, false);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        mRootLayout = (RelativeLayout) mView.findViewById(R.id.rootLayout);
        mCheckNowLayout = (RelativeLayout) mView.findViewById(R.id.checkForMoreLayout);
        mCheckNowButton = (Button) mView.findViewById(R.id.checkNowButton);


        LinearLayout votingLayout = (LinearLayout) mView.findViewById(R.id.votingLayout);
        LinearLayout votingLayout2 = (LinearLayout) mView.findViewById(R.id.votingLayout2);
        votingLayout2.setVisibility(View.INVISIBLE);

        mVoteCounter = (ImageView) mView.findViewById(R.id.voteCounter);

        mLoadingLayout = (RelativeLayout) mView.findViewById(R.id.loadImageProgressBar);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mVotingImages.add(0, (RelativeLayout) mView.findViewById(R.id.voteImage1));
        mVotingImages.add(1, (RelativeLayout) mView.findViewById(R.id.voteImage2));
        mVotingImages.add(2, (RelativeLayout) mView.findViewById(R.id.voteImage3));
        mVotingImages.add(3, (RelativeLayout) mView.findViewById(R.id.voteImage4));


        mPairSwitch = 0;
        mTopImagePosition = 0;
        mBottomImagePosition = 1;

        ArrayList<RoundedImageView> votingImageIds = new ArrayList<>();
        for(int i = 0; i < mVotingImages.size(); i++) {
            votingImageIds.add((RoundedImageView) mVotingImages.get(i).getChildAt(0));
        }
        mImagePuller = new ParseImageHelper(votingImageIds, mView.getContext(), mLoadingLayout, mCheckNowLayout, mRootLayout);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser!=null)
        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e!=null) {
                    Log.d(TAG, "No current user");
                    return;
                }

                mUserBatch = currentUser.getParseObject(ParseConstants.KEY_BATCH);
                if(mUserBatch != null) {
                    mUserBatch.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject userBatch, ParseException e) {

                            if(userBatch.get(ParseConstants.KEY_ACTIVE) == false) {
                                mVoteCount = 0;
                                mVotesNeeded = 0;
                                mCounterPosition = 0;

                            } else {
                                mVoteCount = (float) userBatch.getInt(ParseConstants.KEY_USER_VOTES);
                                mVotesNeeded = (float) userBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
                            }

                            mScreenHeight = mRootLayout.getHeight();
                            mIncrement =  (mVotesNeeded != 0)? mScreenHeight / mVotesNeeded : 0;
                            mCounterPosition = -(mVoteCount *mIncrement);

                            Log.d(TAG, "COUNTER POSITION :    " + mCounterPosition);
                            if(mCounterPosition < 0) {
                                AnimatorSet set = new AnimatorSet();
                                set.playTogether(
                                        Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(mVoteCounter, "translationY", 0, mCounterPosition))
                                );
                                set.setDuration(BestieConstants.ANIMATION_DURATION);
                                set.start();
                            }

                        }
                    });
                }

                mImagePuller.pullVoteImages(0);
            }
        });

        mCheckNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckNowLayout.setVisibility(View.INVISIBLE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                mImagePuller.pullVoteImages(0);
            }
        });


        setListeners(mVotingImages, votingLayout, votingLayout2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                for (int i = 0; i < mVotingImages.size(); i++) {
                    mVotingImages.get(i).setScaleX(1.0f);
                    mVotingImages.get(i).setScaleY(1.0f);
                    mVotingImages.get(i).setEnabled(true);
                }
            }
        });

        return mView;
    }

    private void setListeners(List<RelativeLayout> votingImages, LinearLayout votingLayout, LinearLayout votingLayout2) {
        for(int i = 0; i < votingImages.size(); i++) {
            if(i <= 1) {
                votingImages.get(i).setOnClickListener(nextImagesTransition(votingLayout, votingLayout2));
            } else {
                votingImages.get(i).setOnClickListener(nextImagesTransition(votingLayout2, votingLayout));
            }

            if(i%2 == 0) {
                votingImages.get(i).setOnTouchListener(onTouchFrame(votingImages.get(i), votingImages.get(i+1)));
            } else {
                votingImages.get(i).setOnTouchListener(onTouchFrame(votingImages.get(i), votingImages.get(i-1)));

            }
        }
    }

    private void disableVoteImages(boolean isDisabled) {
        for(int i = 0; i < mVotingImages.size(); i++) {
            if(isDisabled) mVotingImages.get(i).setEnabled(false);
            else mVotingImages.get(i).setEnabled(true);
        }
    }

    private View.OnClickListener nextImagesTransition(final LinearLayout startLayout, final LinearLayout endLayout) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIncrement =  (mVotesNeeded != 0)? mScreenHeight / mVotesNeeded : 0;

                if (v.getId() == R.id.voteImage1 || v.getId() == R.id.voteImage3 ) {
                    mImagePuller.setImageVoted(mTopImagePosition);
                    // run with top position
                } else {
                    mImagePuller.setImageVoted(mBottomImagePosition);
                    // run with bottom position
                }

                disableVoteImages(true);
                startLayout.setEnabled(false);
                endLayout.setEnabled(false);
                if (BestieConstants.ACTIVE_VOTE_COUNT) mVoteCount++;
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(startLayout, "translationY", 0, -2100))
                );

                set.setDuration(BestieConstants.ANIMATION_DURATION);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AnimatorSet set = new AnimatorSet();

                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                        endLayout, "translationY", 1950, 0)),
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                        mVoteCounter, "translationY", mCounterPosition, -(mIncrement * mVoteCount)
                                ))
                        );
                        set.setDuration(BestieConstants.ANIMATION_DURATION);
                        set.start();
                        mCounterPosition = -(mIncrement * mVoteCount);

                            /* RE-ENABLE FOR KEEPING VOTE COUNT BAR UP */

                        if (-mCounterPosition >= mScreenHeight) {
                            if (BestieConstants.ACTIVE_VOTE_COUNT)
                                Toast.makeText(getActivity(), "You've reached the minimum number of votes!", Toast.LENGTH_SHORT).show();
                            BestieConstants.ACTIVE_VOTE_COUNT = false;
                        } else BestieConstants.ACTIVE_VOTE_COUNT = true;

                        // Update mUserBatch;
                        updateBatch();

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        disableVoteImages(false);
                        startLayout.setEnabled(true);
                        endLayout.setEnabled(true);
                        mImagePuller.loadNextPair(mPairSwitch);
                        if (mPairSwitch == 0) {
                            mPairSwitch = 1;
                        } else {
                            mPairSwitch = 0;
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                endLayout.setVisibility(View.VISIBLE);
                mTopImagePosition = mTopImagePosition + 2;
                mBottomImagePosition = mBottomImagePosition + 2;
                set.start();

            }
        };
    }


    private View.OnTouchListener onTouchFrame(final RelativeLayout frame1, final RelativeLayout frame2) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    frame2.setEnabled(false);
                    if (inViewInBounds(frame2, x, y)) {
                        frame2.dispatchTouchEvent(event);
                    } else if (inViewInBounds(frame1, x, y)) {
                        scaleOnTouch(event, frame1);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    frame2.setEnabled(true);
                    frame1.setScaleX(1.0f);
                    frame1.setScaleY(1.0f);
                } else if (inViewInBounds(frame2, x, y)) {
                    frame2.setEnabled(true);
                    frame1.setScaleX(1.0f);
                    frame1.setScaleY(1.0f);
                }
                return false;
            }
        };
    }


    private boolean inViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }


    private void scaleOnTouch(MotionEvent event, RelativeLayout frame) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            frame.setScaleX(1.05f);
            frame.setScaleY(1.05f);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            frame.setScaleX(1.0f);
            frame.setScaleY(1.0f);
        }
    }

    public void updateBatch() {
        if(mUserBatch != null) {
            mUserBatch.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject userBatch, ParseException e) {
                    if (userBatch != null) {
                        if (userBatch.get(ParseConstants.KEY_ACTIVE) == false) {
                            mVoteCount = 0;
                            mVotesNeeded = 0;
                            mCounterPosition = 0;
                        } else if (userBatch.get(ParseConstants.KEY_ACTIVE) == true && userBatch.getInt(ParseConstants.KEY_USER_VOTES) == 0) {
                            mVoteCount = (float) userBatch.getInt(ParseConstants.KEY_USER_VOTES);
                            mVotesNeeded = (float) userBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
                            mIncrement = (mVotesNeeded != 0) ? mScreenHeight / mVotesNeeded : 0;
                            mCounterPosition = -(mVoteCount * mIncrement);
                        }
                        Log.d(TAG, "UPDATE CHECK VOTE COUNT:  " + mVoteCount);
                    }
                }
            });
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
