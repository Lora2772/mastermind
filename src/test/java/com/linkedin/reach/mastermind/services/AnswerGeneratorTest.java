package com.linkedin.reach.mastermind.services;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerGeneratorTest {

    private AnswerGenerator answerGenerator = new AnswerGenerator();

    @Test
    public void testGeneratedStringHasRightLength() {
        String geneatedString = answerGenerator.generate();

        assertEquals(4, geneatedString.length());
    }

    @Test
    public void testGeneratedStringHasRightRange() {

        for (int i = 0; i < 100;i++) {
            String geneatedString = answerGenerator.generate();
            for (char c: geneatedString.toCharArray()) {
                int number = c - '0';
                if (number < 0 || number > 7) {
                    fail();
                }
            }
        }

    }
}
