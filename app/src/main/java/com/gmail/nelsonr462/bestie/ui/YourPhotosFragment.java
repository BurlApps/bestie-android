package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.dummy.DummyContent;

import butterknife.ButterKnife;


public class YourPhotosFragment extends android.support.v4.app.Fragment {

    private View mView;
    private ListView mListView;
    private FloatingActionButton mActionButton;



    private OnFragmentInteractionListener mListener;


    public YourPhotosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_your_photos, container, false);
        ButterKnife.bind(this, mView);
        mListView = (ListView) mView.findViewById(R.id.photoFragmentListView);
        mListView.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(mView.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));

        mActionButton = (FloatingActionButton) mView.findViewById(R.id.fab);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
