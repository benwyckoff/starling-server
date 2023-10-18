package com.dragontreesoftware.odyssey.gcs;

import com.netflix.hollow.api.consumer.HollowConsumer;

import java.nio.file.Path;

public class OdysseyGcsAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {

    private final Path hollowPath;
    private final long version;

    public OdysseyGcsAnnouncementWatcher(Path hollowPath) {
        this.hollowPath = hollowPath;
        long v = Long.MAX_VALUE;;
        try {
            String name = hollowPath.getFileName().toString();
            v = Long.parseLong(name.substring(name.indexOf("-") + 1));
        } catch(Throwable t) {
        }
        this.version = v;
    }

    @Override
    public long getLatestVersion() {
        return 0;
    }

    @Override
    public void subscribeToUpdates(HollowConsumer hollowConsumer) {

    }
}
