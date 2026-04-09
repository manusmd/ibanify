package com.manuweb.ibanifycore.service.brandfetch;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibanify.brandfetch")
public record BrandfetchProperties(
    String clientId,
    String apiBaseUrl,
    Duration connectTimeout,
    Duration readTimeout,
    String cdnHost) {

  public BrandfetchProperties {
    clientId = clientId == null ? "" : clientId;
    if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
      apiBaseUrl = "https://api.brandfetch.io";
    }
    if (connectTimeout == null) {
      connectTimeout = Duration.ofSeconds(3);
    }
    if (readTimeout == null) {
      readTimeout = Duration.ofSeconds(5);
    }
    if (cdnHost == null || cdnHost.isBlank()) {
      cdnHost = "https://cdn.brandfetch.io";
    }
  }
}
