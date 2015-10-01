package com.gmail.nelsonr462.bestie.helpers;

import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.parse.ParseObject;

/**
 * Created by nelson on 9/30/15.
 */
public class BestieRankHelper {
    private RelativeLayout mHeader;
    private ListView mRankedList;
    private ParseObject mUserBatch;


    public BestieRankHelper(RelativeLayout header, ListView rankedList) {
        mHeader = header;
        mRankedList = rankedList;
        mUserBatch = BestieRankFragment.mUserBatch;

    }


}
