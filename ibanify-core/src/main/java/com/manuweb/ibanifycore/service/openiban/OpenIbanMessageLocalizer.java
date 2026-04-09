package com.manuweb.ibanifycore.service.openiban;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public final class OpenIbanMessageLocalizer {

  private static final Pattern VALIDATION_FAILED =
      Pattern.compile("^validation failed\\.?$", Pattern.CASE_INSENSITIVE);
  private static final Pattern INVALID_BANK_CODE =
      Pattern.compile("^invalid\\s+bank\\s+code:\\s*(.+)$", Pattern.CASE_INSENSITIVE);
  private static final Pattern NO_BIC_FOR_BANK_CODE =
      Pattern.compile(
          "^no\\s+bic\\s+found\\s+for\\s+bank\\s+code:\\s*(.+)$", Pattern.CASE_INSENSITIVE);

  private final MessageSource messageSource;

  public OpenIbanMessageLocalizer(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public List<String> localize(List<String> lines, Locale locale) {
    if (lines == null || lines.isEmpty()) {
      return List.of();
    }
    return lines.stream().map(line -> localizeLine(line, locale)).toList();
  }

  private String localizeLine(String line, Locale locale) {
    String t = line.strip();
    ResolvedMessage resolved = resolveMessage(t);
    if (resolved != null) {
      return messageSource.getMessage(resolved.code(), resolved.args(), t, locale);
    }
    return t;
  }

  private static ResolvedMessage resolveMessage(String t) {
    if (t.isEmpty()) {
      return null;
    }
    Matcher m = VALIDATION_FAILED.matcher(t);
    if (m.matches()) {
      return new ResolvedMessage("openiban.validation_failed", new Object[0]);
    }
    m = INVALID_BANK_CODE.matcher(t);
    if (m.matches()) {
      return new ResolvedMessage(
          "openiban.invalid_bank_code", new Object[] {m.group(1).strip()});
    }
    m = NO_BIC_FOR_BANK_CODE.matcher(t);
    if (m.matches()) {
      return new ResolvedMessage(
          "openiban.no_bic_for_bank_code", new Object[] {m.group(1).strip()});
    }
    if (matchesInvalidChecksum(t)) {
      return new ResolvedMessage("openiban.invalid_checksum", new Object[0]);
    }
    if (matchesInvalidLength(t)) {
      return new ResolvedMessage("openiban.invalid_length", new Object[0]);
    }
    if (matchesBankCodeValid(t)) {
      return new ResolvedMessage("openiban.bank_code_valid", new Object[0]);
    }
    if (matchesChecksumValid(t)) {
      return new ResolvedMessage("openiban.checksum_valid", new Object[0]);
    }
    if (matchesIbanValid(t)) {
      return new ResolvedMessage("openiban.iban_valid", new Object[0]);
    }
    return null;
  }

  private static boolean matchesBankCodeValid(String t) {
    return t.regionMatches(true, 0, "bank code valid:", 0, 16);
  }

  private static boolean matchesChecksumValid(String t) {
    if (containsIgnoreCase(t, "invalid")) {
      return false;
    }
    return t.regionMatches(true, 0, "checksum", 0, 8) && containsIgnoreCase(t, "valid");
  }

  private static boolean matchesIbanValid(String t) {
    if (containsIgnoreCase(t, "invalid")) {
      return false;
    }
    String lower = t.toLowerCase(Locale.ROOT);
    return lower.matches("^iban( is)? valid.*");
  }

  private static boolean matchesInvalidChecksum(String t) {
    return containsIgnoreCase(t, "invalid checksum");
  }

  private static boolean matchesInvalidLength(String t) {
    return containsIgnoreCase(t, "invalid") && containsIgnoreCase(t, "length");
  }

  private static boolean containsIgnoreCase(String haystack, String needle) {
    return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
  }

  private record ResolvedMessage(String code, Object[] args) {}
}
