package com.linkedin.reach.mastermind.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PublicApiAnswerGeneratorTest {

    @Mock
    JavaAnswerGenerator javaAnswerGenerator;

    @InjectMocks
    private PublicApiAnswerGenerator publicApiAnswerGenerator;

    @Test
    public void testGeneratedStringHasRightLength() {
        String geneatedString = publicApiAnswerGenerator.generate();

        assertEquals(4, geneatedString.length());
    }

    @Test
    public void testGeneratedStringHasRightRange() {

        for (int i = 0; i < 100;i++) {
            String geneatedString = publicApiAnswerGenerator.generate();
            for (char c: geneatedString.toCharArray()) {
                int number = c - '0';
                if (number < 0 || number > 7) {
                    fail();
                }
            }
        }

    }
}
