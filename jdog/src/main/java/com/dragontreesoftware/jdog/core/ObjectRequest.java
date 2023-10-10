package com.dragontreesoftware.jdog.core;

import java.util.HashMap;
import java.util.Map;

public class ObjectRequest {

    private String type;
    private Integer size;
    private Integer width;
    private Integer height;
    private char[] alphabet;
    private Map<String,String> options;

    public ObjectRequest(ObjectType type, int size) {
        this.type = type.name();
        this.size = size;
    }

    public ObjectRequest(ObjectType type, Map<String,String> options) {
        this.type = type.name();
        this.options = options;
        this.parseAndSetOptions();
        this.enforceValidOptions();
    }

    public String getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type.name();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public char[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(char[] alphabet) {
        this.alphabet = alphabet;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public Map<String, String> getOrCreateOptions() {
        if(options == null) {
            options = new HashMap<>();
        }
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
        parseAndSetOptions();
        enforceValidOptions();
    }

    public String getOption(String key) {
        String opt = null;
        if(options != null) {
            opt = options.get(key);
        }
        return opt;
    }

    public void setOption(String key, String value) {
        getOrCreateOptions().put(key, value);
    }

    public Integer getIntegerOptionOrDefault(String key, Integer defaultVal) {
        Integer val = defaultVal;
        String opt = getOption(key);
        if(opt != null) {
            try {
                val = Integer.parseInt(opt);
            } catch(NumberFormatException e) {
                // ignore
            }
        }
        return val;
    }


    private void parseAndSetOptions() {
        if(options != null) {
            this.size = getIntegerOptionOrDefault("size", size);
            this.width = getIntegerOptionOrDefault("width", width);
            this.height = getIntegerOptionOrDefault("height", height);
        }
    }


    private Integer nullOrPositive(Integer n) {
        if(n == null) {
            return null;
        }
        if(n >= 0) {
            return n;
        }
        return Math.abs(n);
    }

    private void enforceValidOptions() {
        this.size = nullOrPositive(this.size);
        this.width = nullOrPositive(this.width);
        this.height = nullOrPositive(this.height);
    }
}
