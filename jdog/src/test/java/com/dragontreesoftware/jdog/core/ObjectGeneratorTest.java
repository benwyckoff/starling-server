package com.dragontreesoftware.jdog.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ObjectGeneratorTest {

    private static final ObjectGenerator GENERATOR = new ObjectGenerator();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void getPlainText() throws IOException {
        ObjectRequest request = new ObjectRequest(ObjectType.PLAIN_TEXT, 17);
        ObjectResponse response = GENERATOR.getObject(request);

        assertEquals(17, response.getSize());
        assertEquals(ResponseType.PLAIN_TEXT, response.getType());
    }

    @Test
    void getPlainTextWithAlphabet() throws IOException {
        ObjectRequest request = new ObjectRequest(ObjectType.PLAIN_TEXT, 5);
        request.setAlphabet(new char[]{'x', 'y', 'z'});
        ObjectResponse response = GENERATOR.getObject(request);

        assertEquals(5, response.getSize());
        assertEquals(ResponseType.PLAIN_TEXT, response.getType());
        String bodyString = new String(response.getBody(), StandardCharsets.UTF_8);
        assertEquals("xyzxy", bodyString);
    }
}