package com.dragontreesoftware.odyssey.service;

import com.dragontreesoftware.odyssey.core.HollowReader;
import com.dragontreesoftware.odyssey.core.HollowReaders;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.schema.HollowSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HollowReaderService {

    private final HollowReaders hollowReaders;

    @Value("${HollowReaderService.readerTtlSeconds:600}")
    private String readerTtlSeconds;

    public HollowReaderService() {
        hollowReaders = new HollowReaders(HollowReaders.getDefaultHollowPaths());
    }

    @PostConstruct
    void postConstruct() {
        hollowReaders.setIdleTime(Integer.parseInt(readerTtlSeconds), TimeUnit.SECONDS);
    }

    public List<String> getTypes() {
        return hollowReaders.getTypes();
    }

    public List<String> getTypedKeys(String type) {
        return hollowReaders.getTypedKeys(type);
    }

    public List<String> getTypedKeys(String type, int from, int numKeys) {
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
