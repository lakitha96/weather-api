package com.zai.weather.service;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.provider.impl.OpenWeatherMapProvider;
import com.zai.weather.provider.impl.WeatherStackProvider;
import com.zai.weather.service.impl.WeatherReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author lakithaprabudh
 */
public class WeatherReportServiceImplTest {

    private WeatherStackProvider primaryProvider;
    private OpenWeatherMapProvider fallbackProvider;
    private WeatherReportServiceImpl service;

    @BeforeEach
    void setUp() {
        WebClient mockWebClient = mock(WebClient.class);

        primaryProvider = Mockito.spy(new WeatherStackProvider("api.weatherstack.com", "dummyKey", mockWebClient));
        fallbackProvider = Mockito.spy(new OpenWeatherMapProvider("api.openweathermap.org", "AU", "dummyKey", mockWebClient));

        service = new WeatherReportServiceImpl(List.of(primaryProvider, fallbackProvider));
    }

    @Test
    void shouldReturnWeatherFromPrimaryProvider() throws Exception {
        WeatherResponse mockResponse = new WeatherResponse(25.0, 10.0);
        doReturn(mockResponse).when(primaryProvider).fetchWeather("Melbourne");

        WeatherResponse result = service.getWeather("Melbourne");

        assertNotNull(result);
        assertEquals(25.0, result.getTemperatureDegrees());
        assertEquals(10.0, result.getWindSpeed());

        verify(primaryProvider).fetchWeather("Melbourne");
        verify(fallbackProvider, never()).fetchWeather(any());
    }

    @Test
    void shouldReturnWeatherFromFallback_WhenPrimaryFails() throws Exception {
        doThrow(new WeatherServiceException("Primary failed")).when(primaryProvider).fetchWeather("Melbourne");

        WeatherResponse fallbackResponse = new WeatherResponse(22.0, 7.0);
        doReturn(fallbackResponse).when(fallbackProvider).fetchWeather("Melbourne");

        WeatherResponse result = service.getWeather("Melbourne");

        assertNotNull(result);
        assertEquals(22.0, result.getTemperatureDegrees());
        assertEquals(7.0, result.getWindSpeed());

        verify(primaryProvider).fetchWeather("Melbourne");
        verify(fallbackProvider).fetchWeather("Melbourne");
    }

    @Test
    void shouldThrow_WhenAllProvidersFail_AndNoCacheAvailable() throws Exception {
        doThrow(new WeatherServiceException("Primary failed")).when(primaryProvider).fetchWeather("Melbourne");
        doThrow(new WeatherServiceException("Fallback failed")).when(fallbackProvider).fetchWeather("Melbourne");

        WeatherServiceException exception = assertThrows(WeatherServiceException.class,
                () -> service.getWeather("Melbourne"));

        assertTrue(exception.getMessage().contains("All providers failed"));
        verify(primaryProvider).fetchWeather("Melbourne");
        verify(fallbackProvider).fetchWeather("Melbourne");
    }

    @Test
    void shouldReturnFreshCache_WithoutCallingProviders() throws Exception {
        // First call to populate cache
        WeatherResponse freshResponse = new WeatherResponse(26.0, 8.5);
        doReturn(freshResponse).when(primaryProvider).fetchWeather("Melbourne");
        WeatherResponse result1 = service.getWeather("Melbourne");

        assertNotNull(result1);
        assertEquals(26.0, result1.getTemperatureDegrees());
        assertEquals(8.5, result1.getWindSpeed());

        // Second call within 3 seconds - should use fresh cache
        WeatherResponse result2 = service.getWeather("Melbourne");

        assertNotNull(result2);
        assertEquals(26.0, result2.getTemperatureDegrees());
        assertEquals(8.5, result2.getWindSpeed());

        // Only one call to providers overall
        verify(primaryProvider, times(1)).fetchWeather("Melbourne");
        verify(fallbackProvider, never()).fetchWeather(any());
    }

    @Test
    void shouldReturnCachedResponse_WhenAllProvidersFail() throws Exception {
        // Step 1: Return valid response from primary provider to cache it
        WeatherResponse cachedResponse = new WeatherResponse(18.5, 4.2);
        doReturn(cachedResponse).when(primaryProvider).fetchWeather("Melbourne");
        WeatherResponse firstCall = service.getWeather("Melbourne");

        assertNotNull(firstCall);
        assertEquals(18.5, firstCall.getTemperatureDegrees());
        assertEquals(4.2, firstCall.getWindSpeed());

        // Step 2: Simulate all providers failing
        doThrow(new WeatherServiceException("Primary down")).when(primaryProvider).fetchWeather("Melbourne");
        doThrow(new WeatherServiceException("Fallback down")).when(fallbackProvider).fetchWeather("Melbourne");

        // Step 3: Fetch again after 3 seconds to trigger stale cache fallback
        Thread.sleep(3100);
        WeatherResponse cachedCall = service.getWeather("Melbourne");

        assertNotNull(cachedCall);
        assertEquals(18.5, cachedCall.getTemperatureDegrees());
        assertEquals(4.2, cachedCall.getWindSpeed());

        verify(primaryProvider, times(2)).fetchWeather("Melbourne");
        verify(fallbackProvider).fetchWeather("Melbourne");
    }
}