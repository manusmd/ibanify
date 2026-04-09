package com.manuweb.ibanifycore.service.openiban;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iban.openiban")
public record OpenIbanProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout,
    boolean getBic,
    boolean validateBankCode) {}
