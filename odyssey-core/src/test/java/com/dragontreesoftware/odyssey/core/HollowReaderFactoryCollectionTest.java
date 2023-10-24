package com.dragontreesoftware.odyssey.core;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HollowReaderFactoryCollectionTest {

    @Test
    void getName() {
        HollowReaderFactoryCollection c = new HollowReaderFactoryCollection();
        assertTrue(c.getName().startsWith("HollowReaderFactoryCollection"));
    }

    @Test
    void getNames() {
        HollowReaderFactoryCollection c = new HollowReaderFactoryCollection();

        NamedTestFactory f1 = new NamedTestFactory("f1");
        NamedTestFactory f2 = new NamedTestFactory("f2");
        NamedTestFactory f1dup = new NamedTestFactory("f1");

        c.withFactory(f1);

        Set<String> names = new HashSet<>(c.getNames());
        assertTrue(names.contains("f1"));
        assertFalse(names.contains("f2"));

        c.withFactory(f2);
        names = new HashSet<>(c.getNames());
        assertTrue(names.contains("f1"));
        assertTrue(names.contains("f2"));

        c.withFactory(f1dup);
        names = new HashSet<>(c.getNames());
        assertTrue(names.contains("f1"));
        assertTrue(names.contains("f2"));

        assertEquals(2, c.getNames().size());
    }

    private static final class NamedTestFactory implements HollowReaderFactory {

        private final String name;

        public NamedTestFactory(String name) {
            this.name = name;
        }

        @Override
        public HollowReader<?> load(Path p) {
            return null;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}