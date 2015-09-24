package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;


public class VoteFragment extends android.support.v4.app.Fragment {

    private View mView;
    private LinearLayout mVotingLayout;
    private LinearLayout mVotingLayout2;
//    private FrameLayout[] mVotingImages;
    private List<FrameLayout> mVotingImages = new ArrayList<>();
    private int mDuration = 450;

    Rect outRect = new Rect();
    int[] location = new int[2];


    private OnFragmentInteractionListener mListener;

    public VoteFragment() {
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
        mView =  inflater.inflate(R.layout.fragment_vote, container, false);

        mVotingLayout = (LinearLayout) mView.findViewById(R.id.votingLayout);
        mVotingLayout2 = (LinearLayout) mView.findViewById(R.id.votingLayout2);

        mVotingImages.add(0, (FrameLayout) mView.findViewById(R.id.voteImage1));
        mVotingImages.add(1, (FrameLayout) mView.findViewById(R.id.voteImage2));
        mVotingImages.add(2, (FrameLayout) mView.findViewById(R.id.voteImage3));
        mVotingImages.add(3, (FrameLayout) mView.findViewById(R.id.voteImage4));

        mVotingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        Glider.glide(Skill.ExpoEaseOut, 300, ObjectAnimator.ofFloat(mVotingLayout, "translationY", 0, -1900))
                );
                set.setDuration(mDuration);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(mVotingLayout2, "translationY", 1900, 0))
                        );
                        set.setDuration(mDuration);
                        set.start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

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
        });



        mVotingLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        Glider.glide(Skill.ExpoEaseOut, 300, ObjectAnimator.ofFloat(mVotingLayout2, "translationY", 0, -1900))
                );
                set.setDuration(mDuration);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(mVotingLayout, "translationY", 1900, 0))
                        );
                        set.setDuration(mDuration);
                        set.start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

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
        });





        mVotingImages.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mVotingImages.get(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(inViewInBounds(mVotingImages.get(1), x, y)) {
                        mVotingImages.get(1).dispatchTouchEvent(event);
                    } else if(inViewInBounds(mVotingImages.get(0), x, y)) {
                        scaleOnTouch(event, mVotingImages.get(0));
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    mVotingImages.get(0).setScaleX(1.0f);
                    mVotingImages.get(0).setScaleY(1.0f);
                } else if(inViewInBounds(mVotingImages.get(1), x, y)) {
                    mVotingImages.get(0).setScaleX(1.0f);
                    mVotingImages.get(0).setScaleY(1.0f);
                }

                return false;
            }
        });




        mVotingImages.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mVotingImages.get(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                onTouchFrame(event, mVotingImages.get(1), mVotingImages.get(0));

                return false;
            }
        });




        mVotingImages.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mVotingImages.get(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(inViewInBounds(mVotingImages.get(3), x, y)) {
                        mVotingImages.get(3).dispatchTouchEvent(event);
                    } else if(inViewInBounds(mVotingImages.get(2), x, y)) {
                        scaleOnTouch(event, mVotingImages.get(2));
                    }
                } else  if(event.getAction() == MotionEvent.ACTION_UP){
                    mVotingImages.get(2).setScaleX(1.0f);
                    mVotingImages.get(2).setScaleY(1.0f);
                } else if(inViewInBounds(mVotingImages.get(3), x, y)) {
                    mVotingImages.get(2).setScaleX(1.0f);
                    mVotingImages.get(2).setScaleY(1.0f);
                }

                return false;
            }
        });




        mVotingImages.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mVotingImages.get(3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(inViewInBounds(mVotingImages.get(2), x, y)) {
                        mVotingImages.get(2).dispatchTouchEvent(event);
                    } else if(inViewInBounds(mVotingImages.get(3), x, y)) {
                        scaleOnTouch(event, mVotingImages.get(3));
                    }
                } else  if(event.getAction() == MotionEvent.ACTION_UP){
                    mVotingImages.get(3).setScaleX(1.0f);
                    mVotingImages.get(3).setScaleY(1.0f);
                } else if(inViewInBounds(mVotingImages.get(2), x, y)) {
                    mVotingImages.get(3).setScaleX(1.0f);
                    mVotingImages.get(3).setScaleY(1.0f);
                }

                return false;
            }
        });


        return mView;
    }

    private void onTouchFrame(MotionEvent event, FrameLayout frame1, FrameLayout frame2) {
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(inViewInBounds(frame2, x, y)) {
                frame2.dispatchTouchEvent(event);
            } else if(inViewInBounds(frame1, x, y)) {
                scaleOnTouch(event, frame1);
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            frame1.setScaleX(1.0f);
            frame1.setScaleY(1.0f);
        } else if(inViewInBounds(frame2, x, y)) {
            frame1.setScaleX(1.0f);
            frame1.setScaleY(1.0f);
        }
    }


    private boolean inViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }


    private void scaleOnTouch(MotionEvent event, FrameLayout frame) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            frame.setScaleX(1.1f);
            frame.setScaleY(1.1f);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            frame.setScaleX(1.0f);
            frame.setScaleY(1.0f);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
