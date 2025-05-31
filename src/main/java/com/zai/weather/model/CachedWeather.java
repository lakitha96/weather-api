package com.zai.weather.model;

import java.time.Duration;
import java.time.Instant;

/**
 * @author lakithaprabudh
 */
public class CachedWeather {
    private final WeatherResponse data;
    private final Instant timestamp;

    public CachedWeather(WeatherResponse data, Instant timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public WeatherResponse getData() {
        return data;
    }

    public boolean isFresh() {
        return Duration.between(timestamp, Instant.now()).toSeconds() < 3;
    }
}