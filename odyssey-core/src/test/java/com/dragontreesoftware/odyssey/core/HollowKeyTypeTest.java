package com.dragontreesoftware.odyssey.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HollowKeyTypeTest {

    @Test
    void getName() {

        assertEquals("foo", new HollowKeyType.Builder().with("foo").build().getName());
        assertEquals("foo" + HollowKeyType.SEP + "bar", new HollowKeyType.Builder().with("foo").with("bar").build().getName());
    }

    @Test
    void parse() {

        String name = new HollowKeyType.Builder().with("foo").with("bar").build().getName();
        List<String> parts = HollowKeyType.parse(name);
        assertEquals(2, parts.size());
        assertEquals("foo", parts.get(0));
        assertEquals("bar", parts.get(1));
    }
}