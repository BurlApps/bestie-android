package com.gmail.nelsonr462.bestie.helpers;

import android.graphics.Color;
import android.util.Log;
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

    private ParseObject mActiveBatch;

    private final String mFormat =  "%.0f%%";

    private SeriesItem mInitialSeries;
    private int mInitialSeriesIndex;

    private float mMaxVotes;
    private float mUserVotes;
    private float mVotes;

    private int mGraphPosition;



    public GraphDataHelper(RelativeLayout batchView) {
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

        mActiveBatch = BestieRankFragment.mUserBatch;
        mMaxVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
        mUserVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_USER_VOTES);
        mVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_VOTES);

        setGraphData(mMaxVotes, mUserVotes, mVotes);

    }

    public void setGraphData(float maxVotes, float userVotes, float votes) {
        final float calculatedGraphPosition =   ((userVotes/maxVotes < 1)? (userVotes / maxVotes) : 1) * (votes/maxVotes);

        mGraphPosition = (int) (calculatedGraphPosition * 100.0f);

        mBatchGraph.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(38f)
                .build());

        mInitialSeries = new SeriesItem.Builder(Color.argb(255, 255, 215, 64))
                .setRange(0, 100, 0)
                .setCapRounded(true)
                .setInitialVisibility(false)
                .setLineWidth(38f)
                .build();

        mInitialSeries.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (mFormat.contains("%%")) {
                    float percentFilled = ((currentPosition - mInitialSeries.getMinValue()) / (mInitialSeries.getMaxValue() - mInitialSeries.getMinValue()));
                    mCompletionPercentageText.setText(String.format(mFormat, percentFilled * 100f));
                } else {
                    mCompletionPercentageText.setText(String.format(mFormat, currentPosition));
                }

            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        mInitialSeriesIndex = mBatchGraph.addSeries(mInitialSeries);
        mBatchGraph.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(300)
                .build());

        mBatchGraph.addEvent(new DecoEvent.Builder(mGraphPosition).setIndex(mInitialSeriesIndex).setDelay(2000).build());
    }

    public void updateGraph() {
        if((int) mVotes == mActiveBatch.get(ParseConstants.KEY_MAX_VOTES_BATCH) && (int) mUserVotes >= mActiveBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH)) {
            mBatchViewLayout.setVisibility(View.INVISIBLE);
        }

        if ((int) mVotes != BestieRankFragment.mUserBatch.get(ParseConstants.KEY_VOTES) || (int) mUserVotes != BestieRankFragment.mUserBatch.getInt(ParseConstants.KEY_USER_VOTES)) {
            mMaxVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_MAX_VOTES_BATCH);
            mUserVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_USER_VOTES);
            mVotes = (float) mActiveBatch.getInt(ParseConstants.KEY_VOTES);

            final float calculatedGraphPosition = ((mUserVotes/mMaxVotes < 1)? (mUserVotes / mMaxVotes) : 1) * (mVotes / mMaxVotes);

            final int graphPosition = (int) (calculatedGraphPosition * 100.0f);

            mBatchGraph.addEvent(new DecoEvent.Builder(graphPosition).setIndex(mInitialSeriesIndex).setDelay(0).build());
        }
    }
}
