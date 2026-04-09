package com.manuweb.ibanifycore.service.iban;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class IbanDisplayFormatter {

  private IbanDisplayFormatter() {}

  public static String format(String normalizedIban) {
    if (normalizedIban == null || normalizedIban.isEmpty()) {
      return "";
    }
    String u = normalizedIban.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    if (u.length() < 4) {
      return u;
    }
    String country = u.substring(0, 2);
    String check = u.substring(2, 4);
    String rest = u.substring(4);
    List<String> parts = new ArrayList<>();
    for (int i = 0; i < rest.length(); i += 4) {
      parts.add(rest.substring(i, Math.min(i + 4, rest.length())));
    }
    String tail = String.join(" ", parts);
    return tail.isEmpty() ? country + " " + check : country + " " + check + " " + tail;
  }
}
