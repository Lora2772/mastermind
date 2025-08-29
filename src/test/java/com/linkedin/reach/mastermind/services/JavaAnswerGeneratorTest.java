package com.linkedin.reach.mastermind.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JavaAnswerGeneratorTest {

    private final JavaAnswerGenerator generator = new JavaAnswerGenerator();

    @Test
    void test_generate_shouldReturnFourDigitString() {
        String result = generator.generate();

        assertNotNull(result);
        assertEquals(4, result.length());
    }

    @Test
    void test_generate_shouldContainOnlyDigitsFrom0to7() {
        String result = generator.generate();

        assertTrue(result.matches("[0-7]{4}"));
    }

}