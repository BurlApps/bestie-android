package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.gmail.nelsonr462.bestie.BestieConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.BestieListAdapter;
import com.gmail.nelsonr462.bestie.adapters.UploadGridAdapter;
import com.gmail.nelsonr462.bestie.dummy.DummyContent;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.net.URL;
import java.util.ArrayList;


public class BestieRankFragment extends android.support.v4.app.Fragment {

    private View mView;
    private ListView mRankedPictureList;
    private GridView mUploadGrid;
    private RelativeLayout mBestieHeader;
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
        mBestieHeader = (RelativeLayout) inflater.inflate(R.layout.bestie_top_picture, null, false);

        mRankedPictureList = (ListView) mView.findViewById(R.id.listView);
        mRankedPictureList.addHeaderView(mBestieHeader);
        mRankedPictureList.setAdapter(new BestieListAdapter(getActivity()));

        mUploadGrid = (GridView) mView.findViewById(R.id.photoGridView);
        mUploadGrid.setAdapter(new UploadGridAdapter(getActivity()));

        mStartOverButton = (Button) mView.findViewById(R.id.startOverButton);
        mStartOverButton.setOnClickListener(ButtonClickListener(1));
        mShareButton = (Button) mView.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(ButtonClickListener(2));
        mFindBestieButton = (Button) mView.findViewById(R.id.findNewBestieButton);
        mFindBestieButton.setOnClickListener(ButtonClickListener(3));


        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 2100, 0))
                    );
                    set.setDuration(BestieConstants.ANIMATION_DURATION);
                    addPhotosLayout.setVisibility(View.VISIBLE);
                    set.start();

                } else if(buttonType == 3) {
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            Glider.glide(Skill.ExpoEaseOut, 800, ObjectAnimator.ofFloat(addPhotosLayout, "translationY", 0, 2100))
                    );
                    set.setDuration(BestieConstants.ANIMATION_DURATION);
                    set.start();
                }
            }
        };



        return onClickListener;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
