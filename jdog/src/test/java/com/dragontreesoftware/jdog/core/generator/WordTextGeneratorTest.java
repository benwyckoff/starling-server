package com.dragontreesoftware.jdog.core.generator;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class WordTextGeneratorTest {


    @Test
    void generateShine() {
        StringBuilder bob = new StringBuilder();
        WordTextGenerator.buildBody(bob, WordTextGenerator.ALL_WORK_NO_PLAY_WORDS, 150, 50, String::toUpperCase);

        String shine = bob.toString();
        System.out.println(shine);

        assertEquals(150, shine.length());
        assertTrue(shine.toLowerCase(Locale.ROOT).startsWith("all work and no play"));
    }

    @Test
    void generateFoxMixify() {
        StringBuilder bob = new StringBuilder();
        WordTextGenerator.buildBody(bob, WordTextGenerator.QUICK_BROWN_FOX_WORDS, 152, 50, WordTextGenerator::mixifyWord);

        String fox = bob.toString();
        System.out.println(fox);

        assertEquals(152, fox.length());
        assertTrue(fox.toLowerCase(Locale.ROOT).startsWith("the quick brown fox"));
    }

}