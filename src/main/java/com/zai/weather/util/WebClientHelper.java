package com.zai.weather.util;

import com.zai.weather.exception.WeatherServiceException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author lakithaprabudh
 */
public final class WebClientHelper {

    private WebClientHelper() {
    }

    public static String getJsonResponse(WebClient webClient, String uri, String sourceName) throws WeatherServiceException {
        try {
            String jsonResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (jsonResponse == null) {
                throw new WeatherServiceException(sourceName + ": Received empty or invalid response body from the API.");
            }

            return jsonResponse;

        } catch (WebClientResponseException e) {
            throw new WeatherServiceException(sourceName + ": HTTP error - " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new WeatherServiceException(sourceName + ": Failed to retrieve response", e);
        }
    }
}