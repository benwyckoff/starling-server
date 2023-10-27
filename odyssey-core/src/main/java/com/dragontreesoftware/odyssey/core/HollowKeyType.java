package com.dragontreesoftware.odyssey.core;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HollowKeyType {

    static final String SEP = "~~";
    private static final Pattern SPLITTER = Pattern.compile("~~");

    private final String name;

    private HollowKeyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> parse(String name) {
        return Arrays.stream(SPLITTER.split(name)).toList();
    }

    public static class Builder {
        private final StringBuilder stringBuilder = new StringBuilder();

        public Builder with(String part) {
            if(!stringBuilder.isEmpty()) {
                stringBuilder.append(SEP);
            }
            stringBuilder.append(part);
            return this;
        }

        public HollowKeyType build() {
            return new HollowKeyType(stringBuilder.toString());
        }

    }
}
