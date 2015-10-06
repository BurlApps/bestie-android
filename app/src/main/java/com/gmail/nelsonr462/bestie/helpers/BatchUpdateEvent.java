package com.gmail.nelsonr462.bestie.helpers;

import com.parse.ParseObject;

/**
 * Created by nelson on 10/5/15.
 */
public class BatchUpdateEvent {
    public final ParseObject updatedBatch;

    public BatchUpdateEvent(ParseObject batch) {
        updatedBatch = batch;
    }
}
