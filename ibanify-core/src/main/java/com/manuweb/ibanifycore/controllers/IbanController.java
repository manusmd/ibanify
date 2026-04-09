package com.manuweb.ibanifycore.controllers;

import com.manuweb.ibanifycore.entities.ValidateIbanRequest;
import com.manuweb.ibanifycore.entities.ValidateIbanResponse;
import com.manuweb.ibanifycore.service.iban.IbanValidationService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iban")
public final class IbanController {

  private final IbanValidationService validationService;

  public IbanController(IbanValidationService validationService) {
    this.validationService = validationService;
  }

  @PostMapping("/validate")
  public ValidateIbanResponse validate(
      @Valid @RequestBody ValidateIbanRequest request, Locale locale) {
    return validationService.validate(request, locale);
  }
}
