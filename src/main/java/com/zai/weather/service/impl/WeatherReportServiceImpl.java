package com.zai.weather.service.impl;

import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.model.CachedWeather;
import com.zai.weather.model.WeatherResponse;
import com.zai.weather.provider.WeatherProvider;
import com.zai.weather.service.WeatherReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lakithaprabudh
 */
@Service
public class WeatherReportServiceImpl implements WeatherReportService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherReportServiceImpl.class);

    private final List<WeatherProvider> providers;
    private final Map<String, CachedWeather> cache = new ConcurrentHashMap<>();

    public WeatherReportServiceImpl(List<WeatherProvider> providers) {
        this.providers = providers;
    }

    public WeatherResponse getWeather(String city) throws WeatherServiceException {
        CachedWeather cached = cache.get(city);

        if (isFreshCacheAvailable(cached)) {
            return serveFreshCache(city, cached);
        }

        WeatherResponse response = fetchFromProviders(city);
        if (response != null) {
            cache.put(city, new CachedWeather(response, Instant.now()));
            return response;
        }

        if (cached != null) {
            return serveStaleCache(city, cached);
        }

        throw new WeatherServiceException("All providers failed, and no cached data is available for city: " + city);
    }

    private boolean isFreshCacheAvailable(CachedWeather cached) {
        return cached != null && cached.isFresh();
    }

    private WeatherResponse serveFreshCache(String city, CachedWeather cached) {
        logger.info("Returning fresh cached result for '{}'", city);
        return cached.getData();
    }

    private WeatherResponse fetchFromProviders(String city) {
        for (WeatherProvider provider : providers) {
            try {
                logger.info("Trying provider: {}", provider.getClass().getSimpleName());
                WeatherResponse response = provider.fetchWeather(city);
                if (response != null) {
                    return response;
                }
            } catch (Exception e) {
                logger.warn("Provider {} failed: {}", provider.getClass().getSimpleName(), e.getMessage());
            }
        }
        return null;
    }

    private WeatherResponse serveStaleCache(String city, CachedWeather cached) {
        logger.warn("All providers failed. Returning stale cached data for city '{}'", city);
        return cached.getData();
    }
}