package com.dragontree.jdog.core.generator;

import com.dragontree.jdog.core.ObjectGenerator;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class WordTextGeneratorTest {


    @Test
    void generateShine() {
        StringBuilder bob = new StringBuilder();
        WordTextGenerator.buildBody(bob, WordTextGenerator.ALL_WORK_NO_PLAY_WORDS, 150, 50, w -> w.toUpperCase());

        String shine = bob.toString();
        System.out.println(shine);

        assertEquals(150, shine.length());
        assertTrue(shine.toLowerCase(Locale.ROOT).startsWith("all work and no play"));
    }

    @Test
    void generateFoxMixify() {
        StringBuilder bob = new StringBuilder();
        WordTextGenerator.buildBody(bob, WordTextGenerator.QUICK_BROWN_FOX_WORDS, 152, 50, w -> WordTextGenerator.mixifyWord(w));

        String fox = bob.toString();
        System.out.println(fox);

        assertEquals(152, fox.length());
        assertTrue(fox.toLowerCase(Locale.ROOT).startsWith("the quick brown fox"));
    }

}