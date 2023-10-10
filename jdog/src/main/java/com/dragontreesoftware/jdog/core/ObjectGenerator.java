package com.dragontreesoftware.jdog.core;

import com.dragontreesoftware.jdog.core.generator.ImageGenerator;
import com.dragontreesoftware.jdog.core.generator.JsonGenerator;
import com.dragontreesoftware.jdog.core.generator.PlainTextGenerator;
import com.dragontreesoftware.jdog.core.generator.WordTextGenerator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.dragontreesoftware.jdog.core.ObjectType.*;

// TODO add madLib with sentence structures, articles, verbs, adjectives, nouns. Use word lists
// TODO add "tweet", "sentence", "story"
public class ObjectGenerator {

    private static final ObjectGenerator INSTANCE = new ObjectGenerator();
    private final Map<String, Function<ObjectRequest,ObjectResponse>> responseGenerators = new ConcurrentHashMap<>();

    public static ObjectGenerator instance() {
        return INSTANCE;
    }

    public ObjectGenerator() {
        registerEnum(RANDOM_TEXT, PlainTextGenerator::generatePlainText);
        registerEnum(PLAIN_TEXT, PlainTextGenerator::generatePlainText);
        registerEnum(FOX, WordTextGenerator::generateWordText);
        registerEnum(SHINE, WordTextGenerator::generateWordText);
        registerEnum(JSON, JsonGenerator::generateJson);
        registerEnum(IMAGE, ImageGenerator::generateImage);
    }

    public ObjectResponse getObject(ObjectRequest request) {
        Function<ObjectRequest,ObjectResponse> generator = responseGenerators.get(request.getType());
        if(generator != null) {
            return generator.apply(request);
        }
        return null;
    }

    public void registerGenerator(String type, Function<ObjectRequest,ObjectResponse> generator) {
        responseGenerators.put(type, generator);
    }

    private void registerEnum(ObjectType oType, Function<ObjectRequest,ObjectResponse> generator) {
        responseGenerators.put(oType.getCode(), generator);
        responseGenerators.put(oType.name(), generator);
    }
}
