package com.manuweb.ibanifycore.service.iban;

import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public final class IbanNormalizer {

  private static final Pattern CANONICAL =
      Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$");

  public String normalizeToCanonical(String raw) {
    if (raw == null) {
      throw new InvalidIbanInputException("EMPTY");
    }
    String s = raw.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    if (s.isEmpty()) {
      throw new InvalidIbanInputException("EMPTY");
    }
    if (!CANONICAL.matcher(s).matches()) {
      throw new InvalidIbanInputException("INVALID_FORMAT");
    }
    return s;
  }
}
