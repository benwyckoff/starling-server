package com.dragontreesoftware.odyssey.core;

import java.nio.file.Path;

public class DefaultHollowReaderFactory implements HollowReaderFactory {

    @Override
    public HollowReader<? extends Object> load(Path p) {
        return HollowReader.load(p);
    }

    @Override
    public String getName() {
        return "DefaultHollowReaderFactory";
    }
}
