package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.daimajia.easing.linear.Linear;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;


public class VoteFragment extends android.support.v4.app.Fragment {
    private String TAG = VoteFragment.class.getSimpleName();


    private RelativeLayout mRootLayout;
    private List<FrameLayout> mVotingImages = new ArrayList<>();
    private ImageView mVoteCounter;
    private View mView;

    Rect outRect = new Rect();
    int[] location = new int[2];


    private OnFragmentInteractionListener mListener;

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

        LinearLayout votingLayout = (LinearLayout) mView.findViewById(R.id.votingLayout);
        LinearLayout votingLayout2 = (LinearLayout) mView.findViewById(R.id.votingLayout2);

        mVoteCounter = (ImageView) mView.findViewById(R.id.voteCounter);

        mVotingImages.add(0, (FrameLayout) mView.findViewById(R.id.voteImage1));
        mVotingImages.add(1, (FrameLayout) mView.findViewById(R.id.voteImage2));
        mVotingImages.add(2, (FrameLayout) mView.findViewById(R.id.voteImage3));
        mVotingImages.add(3, (FrameLayout) mView.findViewById(R.id.voteImage4));

        setListeners(mVotingImages, votingLayout, votingLayout2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                /*RE-ENABLE FOR KEEPING VOTE COUNT BAR UP*/
//                BestieConstants.VOTE_COUNT = 0;
//                BestieConstants.ACTIVE_VOTE_COUNT = true;
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

        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BestieConstants.SCREEN_HEIGHT = mRootLayout.getHeight();
                BestieConstants.VOTE_INCREMENT = BestieConstants.SCREEN_HEIGHT / BestieConstants.VOTES_NEEDED;
            }
        });

        return mView;
    }

    private void setListeners(List<FrameLayout> votingImages, LinearLayout votingLayout, LinearLayout votingLayout2) {
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
                disableVoteImages(true);
                startLayout.setEnabled(false);
                endLayout.setEnabled(false);
                if (BestieConstants.ACTIVE_VOTE_COUNT) BestieConstants.VOTE_COUNT++;
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(startLayout, "translationY", 0, -2100))
                );
                set.setDuration(BestieConstants.ANIMATION_DURATION);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AnimatorSet set = new AnimatorSet();
                        if (-BestieConstants.VOTE_COUNTER_POSITION > BestieConstants.SCREEN_HEIGHT) {
                            set.playTogether(
                                    Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                            endLayout, "translationY", 1950, 0)),
                                    Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                            mVoteCounter, "translationY", BestieConstants.VOTE_COUNTER_POSITION,
                                            0))
                            );
                            set.setDuration(BestieConstants.ANIMATION_DURATION);
                            set.start();
                            BestieConstants.VOTE_COUNTER_POSITION = 0;
                            BestieConstants.VOTE_COUNT = 0;
                        } else {
                            set.playTogether(
                                    Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                            endLayout, "translationY", 1950, 0)),
                                    Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(
                                            mVoteCounter, "translationY", BestieConstants.VOTE_COUNTER_POSITION,
                                            -(BestieConstants.VOTE_INCREMENT * BestieConstants.VOTE_COUNT)))
                            );
                            set.setDuration(BestieConstants.ANIMATION_DURATION);
                            set.start();
                            BestieConstants.VOTE_COUNTER_POSITION = -(BestieConstants.VOTE_INCREMENT * BestieConstants.VOTE_COUNT);
                            /* RE-ENABLE FOR KEEPING VOTE COUNT BAR UP */
//                            if (-BestieConstants.VOTE_COUNTER_POSITION > BestieConstants.SCREEN_HEIGHT) {
//                                if (BestieConstants.ACTIVE_VOTE_COUNT)
//                                    Toast.makeText(getActivity(), "You've reached the minimum number of votes!", Toast.LENGTH_SHORT).show();
//                                BestieConstants.ACTIVE_VOTE_COUNT = false;
//                            }

                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        disableVoteImages(false);
                        startLayout.setEnabled(true);
                        endLayout.setEnabled(true);
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
        };
    }


    private View.OnTouchListener onTouchFrame(final FrameLayout frame1, final FrameLayout frame2) {
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


    private void scaleOnTouch(MotionEvent event, FrameLayout frame) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            frame.setScaleX(1.05f);
            frame.setScaleY(1.05f);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            frame.setScaleX(1.0f);
            frame.setScaleY(1.0f);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
