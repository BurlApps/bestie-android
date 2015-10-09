package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.adapters.SettingsAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;


public class SettingsFragment extends android.support.v4.app.Fragment {

    private View mView;
    private ArrayList<ListView> mListViews = new ArrayList<>();
    private ListView mBestieList;
    private ListView mUserList;
    private ListView mLegalList;
    private ArrayList<RelativeLayout> mHeaders = new ArrayList<>();

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false);

        if(ParseUser.getCurrentUser() == null) {
//            navigateToLogin();
            return mView;
        }

        mHeaders.add((RelativeLayout) inflater.inflate(R.layout.header_settings_list, mBestieList, false));
        mHeaders.add((RelativeLayout) inflater.inflate(R.layout.header_settings_list, mUserList, false));
        mHeaders.add((RelativeLayout) inflater.inflate(R.layout.header_settings_list, mLegalList, false));

        // Lists
        mBestieList = (ListView) mView.findViewById(R.id.bestieListView);
        SettingsAdapter bAdapter = new SettingsAdapter(mView.getContext(), 0);
        mBestieList.setAdapter(bAdapter);
        TextView title1 = (TextView) mHeaders.get(0).getChildAt(0);
        title1.setText("Bestie");
        mBestieList.addHeaderView(mHeaders.get(0), null, false);



        mUserList = (ListView) mView.findViewById(R.id.userListView);
        SettingsAdapter uAdapter = new SettingsAdapter(mView.getContext(), 1);
        mUserList.setAdapter(uAdapter);
        TextView title2 = (TextView) mHeaders.get(1).getChildAt(0);
        title2.setText("User Information");
        mUserList.addHeaderView(mHeaders.get(1), null, false);



        mLegalList = (ListView) mView.findViewById(R.id.legalListView);
        SettingsAdapter lAdapter = new SettingsAdapter(mView.getContext(), 2);
        mLegalList.setAdapter(lAdapter);
        TextView title3 = (TextView) mHeaders.get(2).getChildAt(0);
        title3.setText("Legal Stuff");
        mLegalList.addHeaderView(mHeaders.get(2), null, false);



        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

}
