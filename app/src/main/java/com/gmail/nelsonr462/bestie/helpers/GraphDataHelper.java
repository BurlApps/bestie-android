package com.gmail.nelsonr462.bestie.helpers;

import android.graphics.Color;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.ParseConstants;
import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.Format;


public class GraphDataHelper {
    private RelativeLayout mBatchViewLayout;
    private DecoView mBatchGraph;
    private TextView mCompletionPercentageText;
    private ProgressBar mProgressBar;

    private ParseUser mCurrentUser;
    private ParseObject mActiveBatch;


    public GraphDataHelper(RelativeLayout batchView) {
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

//              setGraphData();

            }
        });

        mActiveBatch = BestieRankFragment.mUserBatch;
        float maxVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
        float userVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_USER_VOTES);
        float votes = (float) mActiveBatch.getInt(ParseConstants.KEY_VOTES);

        setGraphData(maxVotes, userVotes, votes);

    // Uservoted/maxVotes * votes/maxVotes

    }

    public void setGraphData(float maxVotes, float userVotes, float votes) {
        final String format =  "%.0f%%";
        final float calculatedGraphPosition =  (userVotes/maxVotes) * (votes/maxVotes);

        final int graphPosition = (int) (calculatedGraphPosition * 100.0f);

        mBatchGraph.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(38f)
                .build());

        final SeriesItem initialSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setCapRounded(true)
                .setInitialVisibility(false)
                .setLineWidth(38f)
                .build();

        initialSeries.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - initialSeries.getMinValue()) / (initialSeries.getMaxValue() - initialSeries.getMinValue()));
                    mCompletionPercentageText.setText(String.format(format, percentFilled * 100f));
                } else {
                    mCompletionPercentageText.setText(String.format(format, currentPosition));
                }

//                mCompletionPercentageText.setText(String.format(format, calculatedGraphPosition * 100));
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        int initialSeriesIndex = mBatchGraph.addSeries(initialSeries);
        mBatchGraph.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(300)
                .build());

        mBatchGraph.addEvent(new DecoEvent.Builder(graphPosition).setIndex(initialSeriesIndex).setDelay(2000).build());
//        mBatchGraph.addEvent(new DecoEvent.Builder(100).setIndex(initialSeriesIndex).setDelay(8000).build());
//        mBatchGraph.addEvent(new DecoEvent.Builder(10).setIndex(initialSeriesIndex).setDelay(12000).build());



    }
}
