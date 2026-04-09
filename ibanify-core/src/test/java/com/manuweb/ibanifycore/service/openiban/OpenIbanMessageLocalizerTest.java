package com.manuweb.ibanifycore.service.openiban;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

class OpenIbanMessageLocalizerTest {

  private final OpenIbanMessageLocalizer localizer = createLocalizer();

  private static OpenIbanMessageLocalizer createLocalizer() {
    ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
    ms.setBasename("messages");
    ms.setDefaultEncoding(StandardCharsets.UTF_8.name());
    ms.setFallbackToSystemLocale(false);
    return new OpenIbanMessageLocalizer(ms);
  }

  @Test
  void localizesValidationFailedInGerman() {
    assertThat(localizer.localize(List.of("Validation failed."), Locale.GERMAN))
        .containsExactly("Die Prüfung ist fehlgeschlagen.");
  }

  @Test
  void localizesInvalidBankCodeWithParameter() {
    assertThat(localizer.localize(List.of("Invalid bank code: 64350071"), Locale.GERMAN))
        .containsExactly("Ungültige Bankleitzahl: 64350071");
  }

  @Test
  void localizesNoBicForBankCode() {
    assertThat(
            localizer.localize(
                List.of("No BIC found for bank code: 64350071"), Locale.GERMAN))
        .containsExactly("Keine BIC für die Bankleitzahl 64350071 gefunden.");
  }

  @Test
  void localizesBankCodeValidAsReturnedByOpenIban() {
    assertThat(
            localizer.localize(List.of("Bank code valid: 37040044"), Locale.GERMAN))
        .containsExactly("Die Bankleitzahl in dieser IBAN ist bekannt.");
  }

  @Test
  void passesThroughUnknownLine() {
    assertThat(localizer.localize(List.of("Some new upstream text."), Locale.GERMAN))
        .containsExactly("Some new upstream text.");
  }
}
