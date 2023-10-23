package com.dragontreesoftware.odyssey.service;

import com.dragontreesoftware.odyssey.core.*;
import com.dragontreesoftware.odyssey.gcs.OdysseyGcsHollowReaderFactory;
import com.dragontreesoftware.odyssey.gcs.OdysseyGcsStorage;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.schema.HollowSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HollowReaderService {



    private final HollowReaders hollowReaders;

    @Value("${HollowReaderService.readerTtlSeconds:600}")
    private String readerTtlSeconds;

    public HollowReaderService() {
        // TODO fix up, configurable
        OdysseyGcsStorage storage = new OdysseyGcsStorage("starling-server-odyssey");

        hollowReaders = new HollowReaders();
        hollowReaders.withHollowReaderFactory(new DefaultHollowReaderFactory())
                .withHollowReaderFactory(new OdysseyGcsHollowReaderFactory(storage));
        hollowReaders.scan(HollowReaders.getDefaultHollowPaths(), true);
        hollowReaders.add(Paths.get("hollow","test", "kit", "snapshot-20231014175638001"));
    }

    @PostConstruct
    void postConstruct() {
        hollowReaders.setIdleTime(Integer.parseInt(readerTtlSeconds), TimeUnit.SECONDS);
    }

    public List<HollowReaderKey> getTypes() {
        return hollowReaders.getTypes();
    }

    public List<HollowReaderKey> getTypedKeys(String type) {
        return hollowReaders.getTypedKeys(type);
    }

    public List<HollowReaderKey> getTypedKeys(String type, int from, int numKeys) {
        return hollowReaders.getTypedKeys(type, from, numKeys);
    }

    public Optional<HollowConsumerMetrics> getMetrics(String type) {
        HollowReader reader = hollowReaders.getHollowReader(type);
        if(reader != null) {
            return Optional.of(reader.getMetrics());
        }
        return Optional.empty();
    }

    public Optional<List<HollowSchema>> getSchemas(String type) {
        HollowReader reader = hollowReaders.getHollowReader(type);
        if(reader != null) {
            return Optional.of(reader.getSchemas());
        }
        return Optional.empty();
    }


    public String get(String type, String id) {
        return hollowReaders.get(type, id);
    }

    public String getFromOrdinal(String type, int id) {
        return hollowReaders.getFromOrdinal(type, id);
    }
}
