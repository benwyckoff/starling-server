package com.dragontreesoftware.jdog.http.web;

import com.dragontreesoftware.jdog.core.ObjectGenerator;
import com.dragontreesoftware.jdog.core.ObjectRequest;
import com.dragontreesoftware.jdog.core.ObjectResponse;
import com.dragontreesoftware.jdog.core.ObjectType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.dragontreesoftware.jdog.core.ObjectType.*;

@Service
public class JDogService {

    private static final Map<String,ObjectType> KNOWN_TYPES = new HashMap<>();
    static {
        for (ObjectType value : values()) {
            KNOWN_TYPES.put(value.getCode(), value);
        }
    }

    private final ObjectGenerator objectGenerator;

    public JDogService() {
        objectGenerator = new ObjectGenerator();
    }

    public ObjectResponse get(Map<String,String> options) throws IOException {
        ObjectRequest request = new ObjectRequest(getTypeOption(options), options);
        request.getOrCreateOptions().putAll(options);
        return objectGenerator.getObject(request);
    }

    ObjectType getTypeOption(Map<String,String> options) {
        String type = options.getOrDefault("type","text").toLowerCase(Locale.ROOT);
        return KNOWN_TYPES.getOrDefault(type, PLAIN_TEXT);
    }
}
