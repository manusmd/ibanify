package com.manuweb.ibanifycore.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibanify.cors")
public record CorsProperties(List<String> allowedOrigins) {}
