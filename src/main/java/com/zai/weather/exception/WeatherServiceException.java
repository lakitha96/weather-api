package com.zai.weather.exception;

/**
 * @author lakithaprabudh
 */
public class WeatherServiceException extends Exception {
    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}