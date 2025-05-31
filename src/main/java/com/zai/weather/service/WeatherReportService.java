package com.zai.weather.service;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;

/**
 * @author lakithaprabudh
 */
public interface WeatherReportService {
    WeatherResponse getWeather(String city) throws WeatherServiceException;
}
