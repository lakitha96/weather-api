package com.zai.weather.provider;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.provider.impl.OpenWeatherMapProvider;
import com.zai.weather.util.WebClientHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * @author lakithaprabudh
 */
public class OpenWeatherMapProviderTest {
    private static final String VALID_JSON = """
        {
            "main": {
                "temp": 10.1
            },
            "wind": {
                "speed": 2.57
            },
            "cod": 200
        }
        """;

    private static final String ERROR_JSON = """
        {
            "cod": 401,
            "message": "Invalid API key"
        }
        """;

    private static final String MALFORMED_JSON = """
        {
            "coord": {
                "lon": 144.9633,
                "lat": -37.814
            }
        }
        """;

    String JSON_MISSING_WIND = """
        {
            "main": {
                "temp": 8.0
            }
        }
        """;

    String JSON_MISSING_MAIN = """
        {
            "wind": {
                "speed": 3.0
            }
        }
        """;

    private OpenWeatherMapProvider provider;

    @BeforeEach
    void setup() {
        WebClient webClient = mock(WebClient.class);
        provider = new OpenWeatherMapProvider("api.openweathermap.org", "AU", "dummyKey", webClient);
    }

    @Test
    void shouldReturnValidWeatherResponse() throws Exception {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(VALID_JSON);

            WeatherResponse response = provider.fetchWeather("Melbourne");

            assertNotNull(response);
            assertEquals(10.1, response.getTemperatureDegrees());
            assertEquals(2.57, response.getWindSpeed());
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
            assertTrue(ex.getMessage().contains("Failed to fetch data from OpenWeatherMap"));
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

    @Test
    void shouldThrow_WhenJsonMissingWind() {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(JSON_MISSING_WIND);

            assertThrows(WeatherServiceException.class, () -> provider.fetchWeather("Melbourne"));
        }
    }

    @Test
    void shouldThrow_WhenJsonMissingMain() {
        try (MockedStatic<WebClientHelper> mockedHelper = mockStatic(WebClientHelper.class)) {
            mockedHelper.when(() ->
                    WebClientHelper.getJsonResponse(any(), anyString(), anyString())
            ).thenReturn(JSON_MISSING_MAIN);

            assertThrows(WeatherServiceException.class, () -> provider.fetchWeather("Melbourne"));
        }
    }
}