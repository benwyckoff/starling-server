package com.dragontree.jdog.core;

public enum ObjectType {
    RANDOM_TEXT("rtext"),
    PLAIN_TEXT("text"),
    JSON("json"),
    IMAGE("image"),
    FOX("fox"),
    SHINE("shine");

    private String code;

    ObjectType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
