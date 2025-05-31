package com.zai.weather.provider;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;

/**
 * @author lakithaprabudh
 */
public interface WeatherProvider {
    WeatherResponse fetchWeather(String city) throws WeatherServiceException;
}
