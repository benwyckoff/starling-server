package com.dragontree.jdog.core.generator;

import com.dragontree.jdog.core.ObjectRequest;

import java.util.Optional;

public class Utils {

    public static int sizeOrDefault(ObjectRequest request, int defaultSize) {
        return Optional.ofNullable(request.getSize()).orElse(defaultSize);
    }

    private Utils() {
    }
}
