package com.dragontreesoftware.odyssey.core;

import java.util.Objects;

public class HollowReaderKey {

    private final String hollowPath;
    private final String idHash;
    private final String type;
    private final String value;

    public HollowReaderKey(String hollowPath, String type) {
        this(hollowPath, type, null);
    }

    public HollowReaderKey(String hollowPath, String type, String value) {
        this.hollowPath = hollowPath;
        this.type = type;
        this.value = value;
        this.idHash = String.valueOf(hollowPath.hashCode());
    }

    public String getHollowPath() {
        return hollowPath;
    }

    public String getIdHash() {
        return idHash;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HollowReaderKey that)) return false;
        return getHollowPath().equals(that.getHollowPath()) && getIdHash().equals(that.getIdHash()) && getType().equals(that.getType()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHollowPath(), getIdHash(), getType(), getValue());
    }
}
