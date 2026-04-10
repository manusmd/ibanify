package com.manuweb.ibanifycore.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibanify.cors")
public class CorsProperties {

  private String allowedOrigins = "http://localhost:5173";

  public void setAllowedOrigins(String allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  public List<String> allowedOrigins() {
    if (allowedOrigins == null || allowedOrigins.isBlank()) {
      return List.of();
    }
    return Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }
}
