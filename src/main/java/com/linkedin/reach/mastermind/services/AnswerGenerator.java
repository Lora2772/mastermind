package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AnswerGenerator {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String generate() {
        String url = "https://www.random.org/integers/"
                + "?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new&replacement=true";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // System.out.println(response.body());
            // matches any whitespace character, including:
            // a regular space (' ')
            // a tab (\t)
            // a newline (\n)
            // a carriage return (\r)
            // a form feed (\f)
            String[] parts = response.body().trim().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                sb.append(part);
            }
            return sb.toString();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch random numbers from Random.org", e);
        }
    }

}
