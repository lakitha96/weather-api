package com.zai.weather.controller;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.service.WeatherReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lakithaprabudh
 */
@RestController
@RequestMapping("/v1/weather")
public class WeatherReportController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherReportController.class);
    private final WeatherReportService weatherReportService;

    public WeatherReportController(WeatherReportService weatherReportService) {
        this.weatherReportService = weatherReportService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam(defaultValue = "Melbourne") String city)
            throws WeatherServiceException {
        logger.info("Received weather request for city: {}", city);
        WeatherResponse response = weatherReportService.getWeather(city);
        return ResponseEntity.ok(response);
    }
}