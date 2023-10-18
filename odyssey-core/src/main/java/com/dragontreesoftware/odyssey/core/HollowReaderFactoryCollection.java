package com.dragontreesoftware.odyssey.core;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class HollowReaderFactoryCollection implements HollowReaderFactory {

    private final List<HollowReaderFactory> factories = new LinkedList<>();

    public HollowReaderFactoryCollection() {
    }

    public HollowReaderFactoryCollection withFactory(HollowReaderFactory f) {
        factories.add(f);
        return this;
    }

    @Override
    public HollowReader<? extends Object> load(Path p) {
        HollowReader<? extends Object> reader = null;
        for(HollowReaderFactory factory : factories) {
            if((reader = factory.load(p)) != null) {
                return reader;
            }
        }
        return null;
    }
}
