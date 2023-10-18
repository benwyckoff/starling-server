package com.dragontreesoftware.odyssey.gcs;

import com.netflix.hollow.api.consumer.HollowConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class OdysseyHollowGcsBlob extends HollowConsumer.Blob {

    private final OdysseyGcsStorage odysseyStorage;
    private final Path path;

    public OdysseyHollowGcsBlob(long toVersion, OdysseyGcsStorage odysseyStorage, Path path) {
        super(toVersion);
        this.path = path;
        this.odysseyStorage = odysseyStorage;
    }

    public OdysseyHollowGcsBlob(long fromVersion, long toVersion, OdysseyGcsStorage odysseyStorage, Path path) {
        super(fromVersion, toVersion);
        this.path = path;
        this.odysseyStorage = odysseyStorage;
    }

    @Override
    public InputStream getInputStream() {
        return odysseyStorage.getInputStream(path);
    }
}
