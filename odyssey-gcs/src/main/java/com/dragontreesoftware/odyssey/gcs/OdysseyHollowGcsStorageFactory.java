package com.dragontreesoftware.odyssey.gcs;

import com.dragontreesoftware.odyssey.core.HollowStorageFactory;
import com.netflix.hollow.api.consumer.HollowConsumer;

import java.nio.file.Path;

public class OdysseyHollowGcsStorageFactory implements HollowStorageFactory {

    private final OdysseyGcsStorage odysseyStorage;

    public OdysseyHollowGcsStorageFactory(OdysseyGcsStorage odysseyStorage) {
        this.odysseyStorage = odysseyStorage;
    }

    @Override
    public boolean validPath(Path hollowPath) {
        return this.odysseyStorage.validPath(hollowPath) && this.odysseyStorage.exists(hollowPath);
    }

    @Override
    public HollowConsumer.BlobRetriever createRetriever(Path hollowPath) {
        return new OdysseyGcsBlobRetriever(odysseyStorage, hollowPath);
    }

    @Override
    public HollowConsumer.AnnouncementWatcher createAnnouncementWatcher(Path hollowPath) {
        return new OdysseyGcsAnnouncementWatcher(hollowPath);
    }
}
