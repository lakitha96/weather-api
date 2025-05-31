package com.zai.weather.util;

import com.zai.weather.exception.WeatherServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author lakithaprabudh
 */
public class WebClientHelperTest {
    @Test
    void shouldReturnJsonResponseSuccessfully() throws WeatherServiceException {
        String expectedJson = "{\"mock\":\"data\"}";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(expectedJson)
                                .build()
                ))
                .build();

        String actualResponse = WebClientHelper.getJsonResponse(webClient, "/mock", "MockProvider");

        assertEquals(expectedJson, actualResponse);
    }

    @Test
    void shouldThrowWeatherServiceException_WhenWebClientResponseExceptionOccurs() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.error(
                        WebClientResponseException.create(404, "Not Found", null, null, null)
                ))
                .build();

        WeatherServiceException exception = assertThrows(WeatherServiceException.class,
                () -> WebClientHelper.getJsonResponse(webClient, "/fail", "MockProvider"));

        assertTrue(exception.getMessage().contains("MockProvider: HTTP error - 404"));
    }

    @Test
    void shouldThrowWeatherServiceException_WhenGenericExceptionOccurs() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.error(new RuntimeException("Timeout")))
                .build();

        WeatherServiceException exception = assertThrows(WeatherServiceException.class,
                () -> WebClientHelper.getJsonResponse(webClient, "/timeout", "MockProvider"));

        assertTrue(exception.getMessage().contains("MockProvider: Failed to retrieve response"));
    }
}
