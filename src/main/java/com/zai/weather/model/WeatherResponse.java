package com.zai.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lakithaprabudh
 */
public class WeatherResponse {
    @JsonProperty("temperature_degrees")
    private double temperatureDegrees;

    @JsonProperty("wind_speed")
    private double windSpeed;

    public WeatherResponse(double temperatureDegrees, double windSpeed) {
        this.temperatureDegrees = temperatureDegrees;
        this.windSpeed = windSpeed;
    }

    public double getTemperatureDegrees() {
        return temperatureDegrees;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
}