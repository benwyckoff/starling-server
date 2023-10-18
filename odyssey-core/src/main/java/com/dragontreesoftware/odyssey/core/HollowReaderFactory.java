package com.dragontreesoftware.odyssey.core;

import java.nio.file.Path;

public interface HollowReaderFactory {

    HollowReader<? extends Object> load(Path p);

}
