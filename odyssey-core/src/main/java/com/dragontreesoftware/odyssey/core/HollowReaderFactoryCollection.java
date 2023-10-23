package com.dragontreesoftware.odyssey.core;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class HollowReaderFactoryCollection implements HollowReaderFactory {

    private static final AtomicLong INSTANCE_COUNTER = new AtomicLong();
    private final List<HollowReaderFactory> factories = new LinkedList<>();

    private final String name = "HollowReaderFactoryCollection-" + INSTANCE_COUNTER.getAndIncrement();

    public HollowReaderFactoryCollection() {
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getNames() {
        return factories.stream().map(HollowReaderFactory::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public HollowReaderFactoryCollection withFactory(HollowReaderFactory factory) {
        long matching = factories.stream().filter(f -> f.getName().equals(factory.getName())).count();
        if(matching == 0) {
            factories.add(factory);
        }
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
