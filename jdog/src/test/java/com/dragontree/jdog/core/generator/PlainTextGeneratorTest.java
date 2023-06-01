package com.dragontree.jdog.core.generator;

import com.dragontree.jdog.core.ObjectRequest;
import com.dragontree.jdog.core.ObjectResponse;
import com.dragontree.jdog.core.ObjectType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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