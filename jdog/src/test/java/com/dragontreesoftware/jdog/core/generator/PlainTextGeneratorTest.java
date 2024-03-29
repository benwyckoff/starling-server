package com.dragontreesoftware.jdog.core.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlainTextGeneratorTest {

    @Test
    void buildBody() {
        char[] alpha = {'a','b','c'};
        StringBuilder body = PlainTextGenerator.buildBody(alpha, 3);
        assertEquals("abc", body.toString());

        body = PlainTextGenerator.buildBody(alpha, 5);
        assertEquals("abcab", body.toString());

        body = PlainTextGenerator.buildBody(99);
        assertEquals(99, body.length());
    }


}