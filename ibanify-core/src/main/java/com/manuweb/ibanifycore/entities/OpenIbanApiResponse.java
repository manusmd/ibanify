package com.manuweb.ibanifycore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenIbanApiResponse(
    boolean valid,
    List<String> messages,
    String iban,
    OpenIbanBankData bankData,
    Map<String, Boolean> checkResults) {}
