package com.manuweb.ibanifycore.entities;

import java.util.List;

public record ValidateIbanResponse(
    String normalizedIban,
    String displayIban,
    boolean valid,
    String bankName,
    String bic,
    String bankCode,
    String bankCity,
    String bankZip,
    String bankLogoUrl,
    List<String> messages) {}
