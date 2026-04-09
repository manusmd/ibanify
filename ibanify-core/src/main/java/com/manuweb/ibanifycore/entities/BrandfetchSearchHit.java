package com.manuweb.ibanifycore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandfetchSearchHit(String icon, String name, String domain, String brandId) {}
