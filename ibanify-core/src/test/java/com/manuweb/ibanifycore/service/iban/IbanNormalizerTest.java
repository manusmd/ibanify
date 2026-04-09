package com.manuweb.ibanifycore.service.iban;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class IbanNormalizerTest {

  private final IbanNormalizer normalizer = new IbanNormalizer();

  @Test
  void stripsSeparatorsAndUppercases() {
    assertThat(normalizer.normalizeToCanonical("de89 3704 0044 0532 0130 00"))
        .isEqualTo("DE89370400440532013000");
  }

  @Test
  void acceptsDotsAndHyphens() {
    assertThat(normalizer.normalizeToCanonical("DE89-3704.0044.0532.0130.00"))
        .isEqualTo("DE89370400440532013000");
  }

  @Test
  void rejectsEmptyAfterStrip() {
    assertThatThrownBy(() -> normalizer.normalizeToCanonical("  --  "))
        .isInstanceOf(InvalidIbanInputException.class)
        .extracting(ex -> ((InvalidIbanInputException) ex).getCode())
        .isEqualTo("EMPTY");
  }

  @Test
  void rejectsBadShape() {
    assertThatThrownBy(() -> normalizer.normalizeToCanonical("DE89"))
        .isInstanceOf(InvalidIbanInputException.class)
        .extracting(ex -> ((InvalidIbanInputException) ex).getCode())
        .isEqualTo("INVALID_FORMAT");
  }
}
