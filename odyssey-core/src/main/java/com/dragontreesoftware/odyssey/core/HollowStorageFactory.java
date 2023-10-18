package com.dragontreesoftware.odyssey.core;

import com.netflix.hollow.api.consumer.HollowConsumer;

import java.nio.file.Path;

public interface HollowStorageFactory {

    boolean validPath(Path hollowPath);

    HollowConsumer.BlobRetriever createRetriever(Path hollowPath);

    HollowConsumer.AnnouncementWatcher createAnnouncementWatcher(Path hollowPath);

}
