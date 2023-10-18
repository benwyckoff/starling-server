package com.dragontreesoftware.odyssey.core;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemAnnouncementWatcher;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;

import java.io.File;
import java.nio.file.Path;

public class HollowFileStorageFactory implements HollowStorageFactory {

    public static final HollowFileStorageFactory INSTANCE = new HollowFileStorageFactory();

    @Override
    public boolean validPath(Path hollowPath) {
        File file = hollowPath.toFile();
        return file.exists()
                && file.isDirectory()
                && file.list((dir, name) -> name.toLowerCase().startsWith("snapshot")).length > 0;
    }

    @Override
    public HollowConsumer.BlobRetriever createRetriever(Path hollowPath) {
        return new HollowFilesystemBlobRetriever(hollowPath);
    }

    @Override
    public HollowConsumer.AnnouncementWatcher createAnnouncementWatcher(Path hollowPath) {
        return new HollowFilesystemAnnouncementWatcher(hollowPath);
    }
}
