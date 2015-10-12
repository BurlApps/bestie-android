package com.gmail.nelsonr462.bestie.events;

import com.parse.ParseObject;


public class BatchUpdateEvent {
    public final ParseObject updatedBatch;

    public BatchUpdateEvent(ParseObject batch) {
        updatedBatch = batch;
    }
}
