package com.zai.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lakithaprabudh
 */
@Configuration
public class AppConfiguration {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}