package com.gmail.nelsonr462.bestie.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.gmail.nelsonr462.bestie.R;

import butterknife.ButterKnife;


public class SettingsFragment extends android.support.v4.app.Fragment {

    private View mView;
    private ListView mListView;

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
        ButterKnife.bind(this, mView);
        mListView = (ListView) mView.findViewById(R.id.bestieListView);
        String[] settingsOptions = {"Terms of Service", "Privacy Policy"};
        ArrayAdapter<String> settingsAdapter = new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_list_item_1, settingsOptions);
        mListView.setAdapter(settingsAdapter);
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

}
