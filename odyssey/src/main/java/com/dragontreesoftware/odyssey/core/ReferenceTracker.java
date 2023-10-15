package com.dragontreesoftware.odyssey.core;

import java.util.concurrent.atomic.AtomicLong;

public class ReferenceTracker implements ReferencedObject {

    private final AtomicLong referenceCount = new AtomicLong();
    private volatile long lastReferenceTimestamp = 0;

    public ReferenceTracker() {
    }

    public void touch() {
        referenceCount.incrementAndGet();
        lastReferenceTimestamp = System.currentTimeMillis();
    }

    public long getReferenceCount() {
        return referenceCount.get();
    }

    public long getLastReferenceTimestamp() {
        return lastReferenceTimestamp;
    }

    public long idleTime(long now) {
        return now - lastReferenceTimestamp;
    }

    public long idle() {
        return idleTime(System.currentTimeMillis());
    }
}
