package com.manuweb.ibanifycore.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.manuweb.ibanifycore.config.I18nConfiguration;
import com.manuweb.ibanifycore.entities.ValidateIbanRequest;
import com.manuweb.ibanifycore.entities.ValidateIbanResponse;
import com.manuweb.ibanifycore.service.iban.IbanValidationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = IbanController.class)
@Import({GlobalExceptionHandler.class, I18nConfiguration.class})
class IbanControllerWebMvcTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IbanValidationService validationService;

  @Test
  void validateReturnsBody() throws Exception {
    when(validationService.validate(any(ValidateIbanRequest.class), any()))
        .thenReturn(
            new ValidateIbanResponse(
                "DE89370400440532013000",
                "DE 89 3704 0044 0532 0130 00",
                true,
                "Commerzbank",
                "COBADEFFXXX",
                "37040044",
                "Köln",
                "50447",
                "https://cdn.brandfetch.io/commerzbank.de/icon.png?c=demo",
                List.of()));

    mockMvc
        .perform(
            post("/api/v1/iban/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\":\"iban\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.normalizedIban").value("DE89370400440532013000"))
        .andExpect(jsonPath("$.displayIban").value("DE 89 3704 0044 0532 0130 00"))
        .andExpect(jsonPath("$.valid").value(true))
        .andExpect(jsonPath("$.bankName").value("Commerzbank"))
        .andExpect(
            jsonPath("$.bankLogoUrl")
                .value("https://cdn.brandfetch.io/commerzbank.de/icon.png?c=demo"));
  }

  @Test
  void invalidRequestReturns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/iban/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void unhandledServiceExceptionReturns500() throws Exception {
    when(validationService.validate(any(ValidateIbanRequest.class), any()))
        .thenThrow(new IllegalStateException("unexpected"));

    mockMvc
        .perform(
            post("/api/v1/iban/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\":\"DE89370400440532013000\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
        .andExpect(jsonPath("$.message").value("Something went wrong."));
  }
}
