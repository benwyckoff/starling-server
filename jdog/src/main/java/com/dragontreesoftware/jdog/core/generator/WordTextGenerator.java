package com.dragontreesoftware.jdog.core.generator;

import com.dragontreesoftware.jdog.core.ObjectRequest;
import com.dragontreesoftware.jdog.core.ObjectResponse;
import com.dragontreesoftware.jdog.core.ObjectType;
import com.dragontreesoftware.jdog.core.ResponseType;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class WordTextGenerator {

    private static final Random RAND = new Random();
    private static final String QUICK_BROWN_FOX = "the quick brown fox jumps over a lazy dog.";
    private static final String ALL_WORK_NO_PLAY = "all work and no play makes jack a dull dog.";
    static final String[] QUICK_BROWN_FOX_WORDS = QUICK_BROWN_FOX.split(" ");
    static final String[] ALL_WORK_NO_PLAY_WORDS = ALL_WORK_NO_PLAY.split(" ");

    public static ObjectResponse generateWordText(ObjectRequest request) {
        ObjectResponse response = new ObjectResponse(ResponseType.PLAIN_TEXT);

        StringBuilder body;
        int width = Optional.ofNullable(request.getWidth()).orElse(Constants.DEFAULT_PAGE_WIDTH);
        if (request.getType().equals(ObjectType.FOX.name())) {
            body = buildBody(new StringBuilder(), QUICK_BROWN_FOX_WORDS, Utils.sizeOrDefault(request, Constants.DEFAULT_SIZE), width, null);
        } else {
            body = buildBody(new StringBuilder(), ALL_WORK_NO_PLAY_WORDS, Utils.sizeOrDefault(request, Constants.DEFAULT_SIZE), width, WordTextGenerator::mixifyWord);
        }
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        response.setBody(bytes);
        response.setSize(bytes.length);
        return response;
    }

    static String nextWord(String[] words, int index) {
        return words[index % words.length];
    }

    static String mixifyWord(String source) {
        int len = source.length();
        int p = RAND.nextInt(len + 1);
        if (p < len) {
            StringBuilder b = new StringBuilder(source.length());
            b.append(source);
            b.setCharAt(p, Character.toUpperCase(source.charAt(p)));
            return b.toString();
        } else {
            return source;
        }
    }

    static StringBuilder buildBody(StringBuilder body, String[] words, int size, int width, Function<String, String> modifier) {
        int index = 0;
        int lastBreak = 0;
        while (body.length() < size) {
            int off = body.length() - lastBreak;
            String next = nextWord(words, index++);
            if ((off + next.length()) > width) {
                body.append('\n');
                lastBreak = body.length();
            } else if (index > 1) {
                body.append(' ');
            }
            body.append(modifier == null ? next : modifier.apply(next));
        }
        body.setLength(size);
        return body;
    }
}
