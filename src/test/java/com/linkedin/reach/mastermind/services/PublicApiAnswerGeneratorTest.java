package com.linkedin.reach.mastermind.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicApiAnswerGeneratorTest {

    @Mock
    JavaAnswerGenerator javaAnswerGenerator;
    @Mock
    HttpClient httpClient;
    @Mock
    HttpResponse<Object> response;

    @InjectMocks
    private PublicApiAnswerGenerator publicApiAnswerGenerator;

    @Test
    public void testGeneratedStringHasRightLength() {
        String expectedDigit = "1234";
        publicApiAnswerGenerator = new PublicApiAnswerGenerator(javaAnswerGenerator);
        when(javaAnswerGenerator.generate()).thenReturn(expectedDigit);

        String geneatedString = publicApiAnswerGenerator.generate();

        assertEquals(4, geneatedString.length());
    }

    @Test
    public void testGeneratedStringByJavaRandomClassWhenPublicApiReturnBadResponse() throws Exception {
        String expectedDigit = "1234";

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(httpClient);

            publicApiAnswerGenerator = new PublicApiAnswerGenerator(javaAnswerGenerator);

            when(httpClient.send(any(), any())).thenReturn(response);
            when(response.body()).thenReturn("Server busy exception");

            when(javaAnswerGenerator.generate()).thenReturn(expectedDigit);
            String generated = publicApiAnswerGenerator.generate();

            // assert length is 4
            assertEquals(4, generated.length());

            // assert the right digits
            assertEquals(expectedDigit, generated);

            // assert javaAnswerGenerator is called
            verify(javaAnswerGenerator, times(1)).generate();
        }
    }

    @Test
    public void testGeneratedStringHasRightRange() {
        String expectedDigit = "1234";
        publicApiAnswerGenerator = new PublicApiAnswerGenerator(javaAnswerGenerator);
        when(javaAnswerGenerator.generate()).thenReturn(expectedDigit);

        for (int i = 0; i < 10;i++) {
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
