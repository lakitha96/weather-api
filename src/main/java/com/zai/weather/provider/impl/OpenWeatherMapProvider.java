package com.zai.weather.provider.impl;

import com.zai.weather.provider.WeatherProvider;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
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
@Order(2)
public class OpenWeatherMapProvider implements WeatherProvider {

    private final String hostUrl;
    private final String country;
    private final String apiKey;
    private final WebClient webClient;

    public OpenWeatherMapProvider(
            @Value("${provider.openweathermap.api.url}") String hostUrl,
            @Value("${provider.openweathermap.api.country}") String country,
            @Value("${provider.openweathermap.api.key}") String apiKey,
            WebClient webClient
    ) {
        this.hostUrl = hostUrl;
        this.country = country;
        this.apiKey = apiKey;
        this.webClient = webClient;
    }

    @Override
    public WeatherResponse fetchWeather(String city) throws WeatherServiceException {
        try {
            String uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(hostUrl)
                    .path("/data/2.5/weather")
                    .queryParam("q", city + "," + country)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            String response = WebClientHelper.getJsonResponse(webClient, uri, "OpenWeatherMap");
            return parseWeatherResponse(response);

        } catch (Exception e) {
            throw new WeatherServiceException("Failed to fetch data from OpenWeatherMap", e);
        }
    }

    private WeatherResponse parseWeatherResponse(String jsonResponse) throws WeatherServiceException {
        JSONObject json = new JSONObject(jsonResponse);

        if (!json.has("main") || !json.has("wind")) {
            throw new WeatherServiceException("Unexpected JSON structure from OpenWeatherMap");
        }

        double temperature = json.getJSONObject("main").getDouble("temp");
        double windSpeed = json.getJSONObject("wind").getDouble("speed");

        return new WeatherResponse(temperature, windSpeed);
    }
}