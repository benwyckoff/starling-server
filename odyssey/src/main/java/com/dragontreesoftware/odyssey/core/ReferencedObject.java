package com.dragontreesoftware.odyssey.core;

interface ReferencedObject {

    void touch();

    long getReferenceCount();

    long getLastReferenceTimestamp();

    long idleTime(long now);

    default long idle()  {
        return idleTime(System.currentTimeMillis());
    }
}
