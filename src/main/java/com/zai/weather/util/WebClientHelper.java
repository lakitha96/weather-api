package com.zai.weather.util;

import com.zai.weather.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author lakithaprabudh
 */
public final class WebClientHelper {
    private static final Logger logger = LoggerFactory.getLogger(WebClientHelper.class);

    private WebClientHelper() {
    }

    public static String getJsonResponse(WebClient webClient, String uri, String sourceName) throws WeatherServiceException {
        try {
            String jsonResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new WeatherServiceException(sourceName + ": Empty or invalid response body from API");
            }

            return jsonResponse;

        } catch (WebClientResponseException e) {
            logger.error("[{}] HTTP error while calling {}: status={}, body={}",
                    sourceName, getSanitizedUri(uri), e.getStatusCode(), e.getResponseBodyAsString());
            throw new WeatherServiceException(sourceName + ": HTTP error - " + e.getStatusCode(), e);

        } catch (Exception e) {
            logger.error("[{}] Unexpected error while calling {}: {}", sourceName, getSanitizedUri(uri), e.getMessage(), e);
            throw new WeatherServiceException(sourceName + ": Failed to retrieve response", e);
        }
    }

    private static String  getSanitizedUri(String uri) {
        return uri.replaceAll("(?i)(access_key|appid)=([^&]+)", "$1=######");
    }
}