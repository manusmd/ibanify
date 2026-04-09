package com.manuweb.ibanifycore.service.iban;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IbanDisplayFormatterTest {

  @Test
  void formatsGermanIban() {
    assertThat(IbanDisplayFormatter.format("DE89370400440532013000"))
        .isEqualTo("DE 89 3704 0044 0532 0130 00");
  }

  @Test
  void shortCanonicalUnchangedWhenUnderFourChars() {
    assertThat(IbanDisplayFormatter.format("DE8")).isEqualTo("DE8");
  }
}
