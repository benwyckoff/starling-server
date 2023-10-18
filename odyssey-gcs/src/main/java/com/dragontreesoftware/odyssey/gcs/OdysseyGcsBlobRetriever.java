package com.dragontreesoftware.odyssey.gcs;

import com.netflix.hollow.api.consumer.HollowConsumer;

import java.nio.file.Path;

public class OdysseyGcsBlobRetriever implements HollowConsumer.BlobRetriever {

    private final OdysseyGcsStorage odysseyStorage;
    private final Path hollowPath;

    public OdysseyGcsBlobRetriever(OdysseyGcsStorage odysseyStorage, Path hollowPath) {
        this.odysseyStorage = odysseyStorage;
        this.hollowPath = hollowPath;
    }

    @Override
    public HollowConsumer.Blob retrieveSnapshotBlob(long l) {
        return new OdysseyHollowGcsBlob(l, odysseyStorage, hollowPath);
    }

    @Override
    public HollowConsumer.Blob retrieveDeltaBlob(long l) {
        return null;
    }

    @Override
    public HollowConsumer.Blob retrieveReverseDeltaBlob(long l) {
        return null;
    }
}
