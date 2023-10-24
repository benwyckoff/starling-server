package com.dragontreesoftware.odyssey.gcs;

import com.dragontreesoftware.odyssey.core.HollowReader;
import com.dragontreesoftware.odyssey.core.HollowReaderFactory;

import java.nio.file.Path;

public class OdysseyGcsHollowReaderFactory implements HollowReaderFactory {

    private final OdysseyGcsStorage odysseyGcsStorage;
    private final OdysseyHollowGcsStorageFactory hollowStorageFactory;

    public OdysseyGcsHollowReaderFactory(OdysseyGcsStorage odysseyGcsStorage) {
        this.odysseyGcsStorage = odysseyGcsStorage;
        hollowStorageFactory = new OdysseyHollowGcsStorageFactory(odysseyGcsStorage);
    }

    @Override
    public HollowReader<?> load(Path p) {
        return HollowReader.load(odysseyGcsStorage.absolutePath(p), hollowStorageFactory);
    }

    @Override
    public String getName() {
        return odysseyGcsStorage.getPathPrefix().toString();
    }
}
