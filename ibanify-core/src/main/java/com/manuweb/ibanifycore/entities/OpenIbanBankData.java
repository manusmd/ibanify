package com.manuweb.ibanifycore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenIbanBankData(
    String bankCode,
    String name,
    String zip,
    String city,
    String bic,
    String website) {}
