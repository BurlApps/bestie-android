package com.gmail.nelsonr462.bestie.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.dummy.DummyContent;

public class AddPhotosActivity extends AppCompatActivity /*implements AddPhotoFragment.OnFragmentInteractionListener*/{

    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        mListView = (ListView) findViewById(R.id.yourPhotosListView);
        mListView.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }





//    @Override
//    public void onFragmentInteraction(String id) {
//
//    }
}
