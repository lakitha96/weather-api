package com.zai.weather.provider.impl;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.provider.WeatherProvider;
import com.zai.weather.util.WebClientHelper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author lakithaprabudh
 */
@Component
@Order(1)
public class WeatherStackProvider implements WeatherProvider {
    private final String hostUrl;
    private final String apiKey;
    private final WebClient webClient;

    public WeatherStackProvider(@Value("${provider.weatherstack.api.url}") String hostUrl,
                                @Value("${provider.weatherstack.api.key}") String apiKey,
                                WebClient webClient) {
        this.hostUrl = hostUrl;
        this.apiKey = apiKey;
        this.webClient = webClient;
    }

    @Override
    public WeatherResponse fetchWeather(String city) throws WeatherServiceException {
        try {
            String uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(hostUrl)
                    .path("/current")
                    .queryParam("access_key", apiKey)
                    .queryParam("query", city)
                    .build()
                    .toUriString();

            String response = WebClientHelper.getJsonResponse(webClient, uri, "WeatherStack");
            return parseWeatherResponse(response);

        } catch (Exception e) {
            throw new WeatherServiceException("Failed to fetch data from WeatherStack", e);
        }
    }

    private WeatherResponse parseWeatherResponse(String json) throws WeatherServiceException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.has("error")) {
            throw new WeatherServiceException("WeatherStack API error: " + jsonObject.getJSONObject("error").toString());
        }

        double temperature = jsonObject.getJSONObject("current").getDouble("temperature");
        double windSpeed = jsonObject.getJSONObject("current").getDouble("wind_speed");

        return new WeatherResponse(temperature, windSpeed);
    }
}