package com.zai.weather.provider;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.provider.impl.WeatherStackProvider;
import com.zai.weather.util.WebClientHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author lakithaprabudh
 */
public class WeatherStackProviderTest {
    private static final String VALID_JSON = """
    {
        "current": {
            "temperature": 9,
            "wind_speed": 5
        }
    }
    """;

    private static final String ERROR_JSON = """
        {
            "error": {
                "code": 104,
                "info": "Invalid API key"
            }
        }
        """;

    private static final String MALFORMED_JSON = """
        {
            "location": {
                "name": "Melbourne"
            }
        }
        """;

    private WeatherStackProvider provider;

    @BeforeEach
    void setup() {
        WebClient webClient = mock(WebClient.class);
        provider = new WeatherStackProvider("api.weatherstack.com", "dummyKey", webClient);
    }

    @Test
    void shouldReturnValidWeatherResponse() throws Exception {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(VALID_JSON);

            WeatherResponse response = provider.fetchWeather("Melbourne");

            assertNotNull(response);
            assertEquals(9, response.getTemperatureDegrees());
            assertEquals(5, response.getWindSpeed());
        }
    }

    @Test
    void shouldThrow_WhenApiReturnsErrorField() {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(ERROR_JSON);

            WeatherServiceException ex = assertThrows(WeatherServiceException.class,
                    () -> provider.fetchWeather("Melbourne"));

            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains("Failed to fetch data from WeatherStack"));
        }
    }

    @Test
    void shouldThrow_WhenJsonIsMalformed() {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(MALFORMED_JSON);

            assertThrows(WeatherServiceException.class, () -> provider.fetchWeather("Melbourne"));
        }
    }

    @Test
    void shouldThrow_WhenWebClientFails() {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenThrow(new RuntimeException("Simulated API failure"));

            assertThrows(WeatherServiceException.class, () -> provider.fetchWeather("Melbourne"));
        }
    }
}