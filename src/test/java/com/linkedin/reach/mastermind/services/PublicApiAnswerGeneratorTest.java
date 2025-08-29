package com.linkedin.reach.mastermind.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicApiAnswerGeneratorTest {

    private PublicApiAnswerGenerator publicApiAnswerGenerator;

    @Mock
    private JavaAnswerGenerator mockJavaAnswerGenerator;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @Captor
    private ArgumentCaptor<HttpRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<HttpResponse.BodyHandler<String>> bodyHandlerCaptor;

    @BeforeEach
    void setUp() {
        publicApiAnswerGenerator = new PublicApiAnswerGenerator(mockJavaAnswerGenerator);
        ReflectionTestUtils.setField(publicApiAnswerGenerator, "httpClient", mockHttpClient);
    }

    @Test
    void test_generate_shouldReturnApiGeneratedString_whenApiCallIsSuccessful() throws IOException, InterruptedException {
        when(mockHttpResponse.body()).thenReturn("1\n2\n3\n4\n");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        String result = publicApiAnswerGenerator.generate();

        assertNotNull(result);
        assertEquals("1234", result);
        verify(mockJavaAnswerGenerator, never()).generate();
        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), bodyHandlerCaptor.capture());

        HttpRequest sent = requestCaptor.getValue();
        assertEquals("GET", sent.method());
        URI uri = sent.uri();
        assertNotNull(uri);
    }

    @Test
    void test_enerate_shouldThrowRuntimeException_whenApiCallThrowsIOException() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        RuntimeException e = assertThrows(RuntimeException.class, () -> publicApiAnswerGenerator.generate());
        assertEquals("Failed to generate random numbers from Random.org", e.getMessage());
        assertEquals(IOException.class, e.getCause().getClass());

        verify(mockJavaAnswerGenerator, never()).generate();
    }


    @Test
    void test_generate_shouldThrowRuntimeException_whenApiCallThrowsInterruptedException() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Request interrupted"));

        RuntimeException e = assertThrows(RuntimeException.class, () -> publicApiAnswerGenerator.generate());
        assertEquals("Failed to generate random numbers from Random.org", e.getMessage());
        assertEquals(InterruptedException.class, e.getCause().getClass());

        verify(mockJavaAnswerGenerator, never()).generate();
    }


    @Test
    void test_generate_shouldReturnJavaGeneratedString_whenApiResponseBodyIsTooShort() throws IOException, InterruptedException {
        when(mockHttpResponse.body()).thenReturn("1\n2\n");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockJavaAnswerGenerator.generate()).thenReturn("3456");

        String result = publicApiAnswerGenerator.generate();

        assertEquals("3456", result);
        verify(mockJavaAnswerGenerator, times(1)).generate();
    }

    @Test
    void test_generate_shouldReturnJavaGeneratedString_whenApiResponseBodyIsEmpty() throws IOException, InterruptedException {
        when(mockHttpResponse.body()).thenReturn("");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockJavaAnswerGenerator.generate()).thenReturn("7890");

        String result = publicApiAnswerGenerator.generate();

        assertEquals("7890", result);
        verify(mockJavaAnswerGenerator, times(1)).generate();
    }

    @Test
    void test_generate_shouldReturnApiProcessedString_whenApiResponseBodyHasNonDigitCharactersAndCorrectLength()
            throws IOException, InterruptedException {
        when(mockHttpResponse.body()).thenReturn("a\nb\nc\nd\n");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        String result = publicApiAnswerGenerator.generate();

        assertEquals("abcd", result);
        verify(mockJavaAnswerGenerator, never()).generate();
    }
}
