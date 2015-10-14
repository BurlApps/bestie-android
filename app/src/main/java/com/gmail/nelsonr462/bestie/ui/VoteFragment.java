package com.gmail.nelsonr462.bestie.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieApplication;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.events.BatchUpdateEvent;
import com.gmail.nelsonr462.bestie.events.ImageFlaggedEvent;
import com.gmail.nelsonr462.bestie.events.ImageVotedEvent;
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

import de.greenrobot.event.EventBus;


public class VoteFragment extends android.support.v4.app.Fragment {
    private final String TAG = VoteFragment.class.getSimpleName();

    private Vibrator mVibrator;

    public ParseObject mUserBatch;

    private RelativeLayout mRootLayout;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mCheckNowLayout;
    private RelativeLayout mUploadOnboard;
    private LinearLayout mVotingLayout1;
    private LinearLayout mVotingLayout2;
    private List<RelativeLayout> mVotingImages = new ArrayList<>();
    private ArrayList<TextView> mPercentView = new ArrayList<>();
    private ArrayList<ImageButton> mFlags = new ArrayList<>();
    private ImageView mVoteCounter;
    private TextView mVoteOnboard1;
    private TextView mVoteOnboard2;
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
    private float mPreviousPosition;

    private boolean mShowSnack;

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

        mVibrator = (Vibrator) mView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        mRootLayout = (RelativeLayout) mView.findViewById(R.id.rootLayout);
        mCheckNowLayout = (RelativeLayout) mView.findViewById(R.id.checkForMoreLayout);
        mCheckNowButton = (Button) mView.findViewById(R.id.checkNowButton);


        mVotingLayout1 = (LinearLayout) mView.findViewById(R.id.votingLayout);
        mVotingLayout2 = (LinearLayout) mView.findViewById(R.id.votingLayout2);
        mVotingLayout2.setVisibility(View.INVISIBLE);

        mVoteCounter = (ImageView) mView.findViewById(R.id.voteCounter);

        mLoadingLayout = (RelativeLayout) mView.findViewById(R.id.loadImageProgressBar);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mVotingImages.add(0, (RelativeLayout) mView.findViewById(R.id.voteImage1));
        mVotingImages.add(1, (RelativeLayout) mView.findViewById(R.id.voteImage2));
        mVotingImages.add(2, (RelativeLayout) mView.findViewById(R.id.voteImage3));
        mVotingImages.add(3, (RelativeLayout) mView.findViewById(R.id.voteImage4));

        mPercentView.add(0, (TextView) mView.findViewById(R.id.percentOverlay1));
        mPercentView.add(1, (TextView) mView.findViewById(R.id.percentOverlay2));
        mPercentView.add(2, (TextView) mView.findViewById(R.id.percentOverlay3));
        mPercentView.add(3, (TextView) mView.findViewById(R.id.percentOverlay4));

        mVoteOnboard1 = (TextView) mView.findViewById(R.id.voteOnboard1);
        mVoteOnboard2 = (TextView) mView.findViewById(R.id.voteOnboard2);
        mUploadOnboard = (RelativeLayout) mView.findViewById(R.id.voteOnboardScreen);

        setFlaggedClickListeners();

        mPairSwitch = 0;
        mTopImagePosition = 0;
        mBottomImagePosition = 1;

        ArrayList<RoundedImageView> votingImageIds = new ArrayList<>();
        for(int i = 0; i < mVotingImages.size(); i++) {
            votingImageIds.add((RoundedImageView) mVotingImages.get(i).getChildAt(0));
        }
        mImagePuller = new ParseImageHelper(votingImageIds, mView.getContext(), mLoadingLayout, mCheckNowLayout, mRootLayout, mPercentView);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser!=null)
        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e!=null) {
                    Log.d(TAG, "No current user");
                    return;
                }
                Log.d(TAG, "USER:  "+currentUser.getObjectId());
                mScreenHeight = mRootLayout.getHeight();
                mUserBatch = currentUser.getParseObject(ParseConstants.KEY_BATCH);
                if(mUserBatch != null) {
                    mUserBatch.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject userBatch, ParseException e) {
                            if (userBatch != null) {

                                if (userBatch.get(ParseConstants.KEY_ACTIVE) == false) {
                                    mShowSnack = false;
                                    mVoteCount = 0;
                                    mVotesNeeded = 0;
                                    mCounterPosition = mScreenHeight;

                                } else {
                                    mShowSnack = (userBatch.getInt(ParseConstants.KEY_USER_VOTES) < userBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH));
                                    mVoteCount = (float) userBatch.getInt(ParseConstants.KEY_USER_VOTES);
                                    mVotesNeeded = (float) userBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
                                }

                                mIncrement = (mVotesNeeded != 0) ? mScreenHeight / mVotesNeeded : 0;
                                if (mVoteCount <= mVotesNeeded)
                                    mCounterPosition = -(mVoteCount * mIncrement);
                                else
                                    mCounterPosition = -(mVotesNeeded * mIncrement);

                                Log.d(TAG, "COUNTER POSITION :    " + mCounterPosition);
                                if (mCounterPosition < 0) {
                                    AnimatorSet set = new AnimatorSet();
                                    set.playTogether(
                                            Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(mVoteCounter, "translationY", 0, mCounterPosition))
                                    );
                                    set.setDuration(BestieConstants.ANIMATION_DURATION);
                                    set.start();
                                }

                                mPreviousPosition = mCounterPosition;

                            }

                        }
                    });
                }

                mImagePuller.pullVoteImages(0);
                if(BestieConstants.VOTE_ONBOARDING_ACTIVE) {
                    mVoteOnboard1.setVisibility(View.VISIBLE);
                    mVoteOnboard2.setVisibility(View.VISIBLE);
                }
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


        setListeners(mVotingImages, mVotingLayout1, mVotingLayout2, mPercentView);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(BestieConstants.UPLOAD_ONBOARDING_ACTIVE && BestieConstants.FROM_FIRST_UPLOAD) {
                    voteOnboarding();
                    BestieConstants.UPLOAD_ONBOARDING_ACTIVE = false;
                    BestieConstants.FROM_FIRST_UPLOAD = false;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                for (int i = 0; i < mVotingImages.size(); i++) {
                    scaleView(mVotingImages.get(i), 1.0f, 1.0f);
                    mVotingImages.get(i).setEnabled(true);
                }
            }
        });


        return mView;
    }

    private void setListeners(List<RelativeLayout> votingImages, LinearLayout votingLayout, LinearLayout votingLayout2, ArrayList<TextView> percentView) {
        for(int i = 0; i < votingImages.size(); i++) {
            if(i <= 1) {
                votingImages.get(i).setOnClickListener(nextImagesTransition(votingLayout, votingLayout2, percentView.get(0), percentView.get(1)));
            } else {
                votingImages.get(i).setOnClickListener(nextImagesTransition(votingLayout2, votingLayout, percentView.get(2), percentView.get(3)));
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

    private View.OnClickListener nextImagesTransition(final LinearLayout startLayout, final LinearLayout endLayout, final TextView percent1, final TextView percent2) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Votes needed:  "+mVotesNeeded);
                Log.d(TAG, "Screen Height:    "+mScreenHeight);

                if(BestieConstants.VOTE_ONBOARDING_ACTIVE) {
                    YoYo.with(Techniques.FadeOut).duration(200).playOn(mVoteOnboard1);
                    YoYo.with(Techniques.FadeOut).duration(200).playOn(mVoteOnboard2);
                    BestieConstants.VOTE_ONBOARDING_ACTIVE = false;
                    BestieApplication.mMixpanel.track("Mobile.Voting Tutorial.Completed");
                }


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

                percent1.setVisibility(View.VISIBLE);
                percent2.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeIn).duration(300).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        YoYo.with(Techniques.FadeIn).duration(300).playOn(percent2);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(startLayout, "translationY", 0, -2100))
                        );

                        set.setDuration(BestieConstants.ANIMATION_DURATION);
                        set.setStartDelay(300);
                        set.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                AnimatorSet set = new AnimatorSet();

                                set.playTogether(
                                        Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                                endLayout, "translationY", 1950, 0))
                                );
                                set.setDuration(BestieConstants.ANIMATION_DURATION);
                                set.setStartDelay(300);
                                set.start();
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Log.d(TAG, "CP:   " + mCounterPosition);

                                percent1.setVisibility(View.INVISIBLE);
                                percent2.setVisibility(View.INVISIBLE);
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

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(percent1);

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
                    if (inViewInBounds(frame2, x, y) && !isFlagTouched(mFlags, x, y)) {
                        frame2.dispatchTouchEvent(event);
                    } else if (inViewInBounds(frame1, x, y)) {
                        scaleOnTouch(event, frame1);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    frame2.setEnabled(true);
                    scaleView(frame1, 1.05f, 1.0f);
                    scaleView(frame1, 1.05f, 1.0f);
                } else if (inViewInBounds(frame2, x, y) && !isFlagTouched(mFlags, x, y)) {
                    frame2.setEnabled(true);
                    scaleView(frame1, 1.05f, 1.0f);
                    scaleView(frame1, 1.05f, 1.0f);
                }
                return false;
            }
        };
    }

    private boolean isFlagTouched(final ArrayList<ImageButton> flags, int x, int y){
        boolean onFlag = false;

        for(int i =0; i < flags.size(); i++) {
            flags.get(i).getDrawingRect(outRect);
            flags.get(i).getLocationOnScreen(location);
            outRect.offset(location[0], location[1]);
            if(outRect.contains(x, y)) onFlag = true;
        }

        return onFlag;
    }

    private boolean inViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void scaleOnTouch(MotionEvent event, RelativeLayout frame) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            scaleView(frame, 1.0f, 1.05f);
            scaleView(frame, 1.0f, 1.05f);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            scaleView(frame, 1.05f, 1.0f);
            scaleView(frame, 1.05f, 1.0f);
        }

    }


    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(150);
        v.startAnimation(anim);
    }


    private void setFlagListeners(ArrayList<ImageButton> flags) {

        for(int i = 0; i < flags.size(); i++) {
            final int positionCheck = i;
            flags.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagePuller.flagImage((positionCheck%2 == 0)? mTopImagePosition : mBottomImagePosition, v);
                }
            });
        }
    }

    public void updateBatch() {

        if(mUserBatch.getBoolean(ParseConstants.KEY_ACTIVE) || mShowSnack ) {

            mVoteCount = (float) mUserBatch.getInt(ParseConstants.KEY_USER_VOTES);
            mVotesNeeded = (float) mUserBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
            mIncrement = (mVotesNeeded != 0) ? mScreenHeight / mVotesNeeded : 0;


            if (mVoteCount <= mVotesNeeded)
                mCounterPosition = -(mVoteCount * mIncrement);
            else if (mVotesNeeded < mVoteCount)
                mCounterPosition = -(mVotesNeeded * mIncrement);
        }

        AnimatorSet set = new AnimatorSet();

        set.playTogether(
                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                        mVoteCounter, "translationY", mPreviousPosition, mCounterPosition
                ))
        );
        set.setDuration(BestieConstants.ANIMATION_DURATION);
        set.start();

        if (mVoteCount >= mVotesNeeded && mUserBatch != null) {
            if (BestieConstants.ACTIVE_VOTE_COUNT && mShowSnack) {
                final Snackbar snackbar = Snackbar.make(mVoteCounter, R.string.bestie_max_votes_reached_message, 5000);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.bestieBlue));

                snackbar.setAction("Okay", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                }).setActionTextColor(getResources().getColor(R.color.bestieYellow)).show();
                mVibrator.vibrate(300);
                mShowSnack = false;
            }
            BestieConstants.ACTIVE_VOTE_COUNT = false;
        } else BestieConstants.ACTIVE_VOTE_COUNT = true;
        mPreviousPosition = mCounterPosition;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    public void newPull(){
        mLoadingLayout.setVisibility(View.VISIBLE);
        mImagePuller.pullVoteImages(3);
    }

    public void onEvent(BatchUpdateEvent event) {
        if(mUserBatch != null) {
            Log.d(TAG, "CURRENT VOTES:   " + mUserBatch.getInt(ParseConstants.KEY_USER_VOTES));
            Log.d(TAG, "NEW VOTES:   " + event.updatedBatch.getInt(ParseConstants.KEY_USER_VOTES));

            if (mVoteCount != event.updatedBatch.getInt(ParseConstants.KEY_USER_VOTES)) {
                mUserBatch = event.updatedBatch;
                updateBatch();
            }
        } else {
            mUserBatch = event.updatedBatch;
            updateBatch();
        }
    }

    public void onEvent(ImageVotedEvent event) {
        Log.d(TAG, "VOTED EVENT VOTE COUNT:  "+event.mVoteCount);
        Log.d(TAG, "VOTED EVENT FINISHED:  "+event.mFinished);
        if(event.mFinished) {
           mShowSnack = true;
        }
    }

    public void onEvent(ImageFlaggedEvent event) {
        final LinearLayout startLayout = (mPairSwitch == 0)? mVotingLayout1 : mVotingLayout2;
        final LinearLayout endLayout = (mPairSwitch == 0)? mVotingLayout2 : mVotingLayout1;

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

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "CP:   " + mCounterPosition);

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

    private void setFlaggedClickListeners() {
        mFlags.add(0, (ImageButton) mView.findViewById(R.id.flag1));
        mFlags.add(1, (ImageButton) mView.findViewById(R.id.flag2));
        mFlags.add(2, (ImageButton) mView.findViewById(R.id.flag3));
        mFlags.add(3, (ImageButton) mView.findViewById(R.id.flag4));

        setFlagListeners(mFlags);

    }

    public void voteOnboarding() {
        mUploadOnboard.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeOut).delay(7000).duration(300).playOn(mUploadOnboard);

        AnimatorSet set = new AnimatorSet();

        set.playTogether(
                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                        mVoteCounter, "translationY", 0, -2100
                ))
        );
        set.setStartDelay(1000);
        set.setDuration(5000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet set = new AnimatorSet();

                set.playTogether(
                        Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                mVoteCounter, "translationY", -2100, 0
                        ))
                );
                set.setDuration(1000);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        BestieApplication.mMixpanel.track("Mobile.Bars Tutorial Completed");
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                set.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

}



