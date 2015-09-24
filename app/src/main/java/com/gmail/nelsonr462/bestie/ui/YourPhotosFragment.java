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

//    @Bind(R.id.editPhotosButton) Button mEditPhotosButton;
//    @Bind(R.id.addPhotosButton) Button mAddPhotosButton;


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




//    @OnClick(R.id.editPhotosButton)
//    public void editPhotos(){
//        Intent intent = new Intent(getActivity(), EditPhotosActivity.class);
//        startActivity(intent);
//
//    }
//
//
//    @OnClick(R.id.addPhotosButton)
//    public void addPhotos(){
//        Intent intent = new Intent(getActivity(), AddPhotosActivity.class);
//        startActivity(intent);
//
//    }



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
