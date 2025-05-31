package com.zai.weather.controller;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.service.WeatherReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author lakithaprabudh
 */
@WebMvcTest(WeatherReportController.class)
public class WeatherReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherReportService weatherReportService;

    @Test
    void shouldReturnWeatherSuccessfully() throws Exception {
        Mockito.when(weatherReportService.getWeather("Melbourne"))
                .thenReturn(new WeatherResponse(22.0, 11.0));

        mockMvc.perform(get("/v1/weather")
                        .param("city", "Melbourne")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature_degrees").value(22.0))
                .andExpect(jsonPath("$.wind_speed").value(11.0));
    }

    @Test
    void shouldReturn503_WhenWeatherServiceFails() throws Exception {
        Mockito.when(weatherReportService.getWeather(anyString()))
                .thenThrow(new WeatherServiceException("Service failed"));

        mockMvc.perform(get("/v1/weather")
                        .param("city", "Melbourne"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturn500_OnUnexpectedError() throws Exception {
        Mockito.when(weatherReportService.getWeather(anyString()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/v1/weather")
                        .param("city", "Melbourne"))
                .andExpect(status().isInternalServerError());
    }
}