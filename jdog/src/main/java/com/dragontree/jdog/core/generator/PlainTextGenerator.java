package com.dragontree.jdog.core.generator;

import com.dragontree.jdog.core.ObjectRequest;
import com.dragontree.jdog.core.ObjectResponse;
import com.dragontree.jdog.core.ObjectType;
import com.dragontree.jdog.core.ResponseType;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class PlainTextGenerator {

    private static final Random RAND = new Random();
    // default text buffer
    private static final int DEFAULT_TEXT_SIZE = 4096;
    private static final StringBuilder DEFAULT_TEXT = new StringBuilder(DEFAULT_TEXT_SIZE);
    static {
        int range = 1 + 'z' - ' ';
        Random rand = new Random();
        for(int i = 0; i < DEFAULT_TEXT_SIZE; i++) {
            DEFAULT_TEXT.append((char)(' ' + rand.nextInt(range)));
        }
        // TODO add log4j
        System.out.println(DEFAULT_TEXT.toString());
    }



    private PlainTextGenerator() {
    }

    public static ObjectResponse generatePlainText(ObjectRequest request) {
        ObjectResponse response = new ObjectResponse(ResponseType.PLAIN_TEXT);
        char[] alphabet = request.getAlphabet();
        if(alphabet == null) {
            alphabet = DEFAULT_TEXT.toString().toCharArray();
        }
        StringBuilder body = buildBody(alphabet, Utils.sizeOrDefault(request, Constants.DEFAULT_SIZE));
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        response.setBody(bytes);
        response.setSize(bytes.length);
        return response;
    }


    static StringBuilder buildBody(int size) {
        return buildBody(new StringBuilder(size), DEFAULT_TEXT.toString().toCharArray(), size, 80);
    }

    static StringBuilder buildBody(char[] alphabet, int size) {
        return buildBody(new StringBuilder(size), alphabet, size, 80);
    }

    static StringBuilder buildBody(StringBuilder body, char[] alphabet, int size, int width) {
        int remainder = size;
        while(remainder > 0) {
            int chunk = Math.min(remainder, alphabet.length);
            body.append(alphabet, 0, chunk);
            remainder -= chunk;
        }
        return insertLineBreaks(body, width);
    }

    static StringBuilder insertLineBreaks(StringBuilder body, int width) {
        int originalSize = body.length();
        if(width > 0 && originalSize > width) {
            int eolPos = width;
            while(eolPos < body.length()) {
                body.insert(eolPos, '\n');
                eolPos += (width + 1);
            }
            body.setLength(originalSize);
        }
        return body;
    }



}
