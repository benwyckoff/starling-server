package com.dragontreesoftware.jdog.core.generator;

import com.dragontreesoftware.jdog.core.ObjectRequest;
import com.dragontreesoftware.jdog.core.ObjectResponse;
import com.dragontreesoftware.jdog.core.ResponseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonGenerator {

    private static final String JSON_WORD_STRING = "the time has come the walrus said to talk of many things of ships and shoes and sealing wax of cabbages and kings";
    static final String[] JSON_WORDS = JSON_WORD_STRING.split(" ");
    private static final String JSON_SMALL_FORMAT = "{ \"data\": \"%s\" }";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int BLOCK_SIZE;
    static {
        Block b = new Block(10, "quick");
        String json = JSON_SMALL_FORMAT;
        try {
            json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(b);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        BLOCK_SIZE = json.length();
    }


    private static final class Block {
        private int index;
        private String data;

        public Block() {
        }

        public Block(int index, String data) {
            this.index = index;
            this.data = data;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    /**
     * Generate a pretty-printed JSON String of an approximate size based on the request.
     * For anything over 100 bytes, the data consists of UUID keys that are highly
     * incompressible mapping to Block values that are highly compressible.
     * @param request
     * @return a pretty-printed JSON String of an approximate size based on the request
     */
    public static ObjectResponse generateJson(ObjectRequest request) {
        int size = Utils.sizeOrDefault(request, 99);
        String json;
        if(size < JSON_SMALL_FORMAT.length()) {
            json = JSON_SMALL_FORMAT.formatted("A");
        } else if(size < 100) {
            int delta = size - JSON_SMALL_FORMAT.length() + 2;
            json = JSON_SMALL_FORMAT.formatted("A".repeat(delta));
        } else {
            // size is approximate for > 100
            Map<String,Object> data = new TreeMap<>();
            int approx = 0;
            int index = 0;
            while(approx < size) {
                for (String word : JSON_WORDS) {
                    if(approx < size) {
                        String key = UUID.randomUUID().toString().substring(0,12);
                        data.put(key, new Block(index++, word));
                        approx += (key.length() + BLOCK_SIZE + 16);
                    }
                }
            }
            try {
                json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            } catch (JsonProcessingException e) {
                json = JSON_SMALL_FORMAT.formatted("ERROR");
            }
        }
        ObjectResponse response = new ObjectResponse(ResponseType.JSON);
        response.setSize(json.length());
        response.setBody(json.getBytes(StandardCharsets.UTF_8));
        return response;
    }

}
