package com.manuweb.ibanifycore.entities;

import jakarta.validation.constraints.NotBlank;

public record ValidateIbanRequest(
    @NotBlank(message = "{iban.validation.input.notblank}") String input) {}
