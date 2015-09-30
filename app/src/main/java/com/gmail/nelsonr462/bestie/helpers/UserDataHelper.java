package com.gmail.nelsonr462.bestie.helpers;

import android.graphics.Color;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by nelson on 9/29/15.
 */
public class UserDataHelper {
    private RelativeLayout mBatchViewLayout;
    private DecoView mBatchGraph;
    private TextView mCompletionPercentageText;
    private ProgressBar mProgressBar;

    private ParseUser mCurrentUser;
    private ParseObject mActiveBatch;


    public UserDataHelper(RelativeLayout batchView) {
        mCurrentUser = ParseUser.getCurrentUser();
        mBatchViewLayout = batchView;

        for(int i = 0; i < mBatchViewLayout.getChildCount(); i++) {
            View v = mBatchViewLayout.getChildAt(i);
            switch (v.getId()) {
                case R.id.batchCompletionGraph:
                    mBatchGraph = (DecoView) v;
                    break;
                case R.id.completionPercentage:
                    mCompletionPercentageText = (TextView) v;
                    break;
                case R.id.batchViewProgressBar:
                    mProgressBar = (ProgressBar) v;
                    break;
            }
        }

        mCurrentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject currentUser, ParseException e) {


                setGraphData();

            }
        });



    }

    private void setGraphData() {
        mBatchGraph.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(38f)
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 80)
                .setLineWidth(38f)
                .build();

        int series1Index = mBatchGraph.addSeries(seriesItem1);

    }
}
