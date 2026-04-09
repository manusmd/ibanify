package com.manuweb.ibanifycore.service.iban;

import com.manuweb.ibanifycore.entities.OpenIbanApiResponse;
import com.manuweb.ibanifycore.entities.OpenIbanBankData;
import com.manuweb.ibanifycore.entities.ValidateIbanRequest;
import com.manuweb.ibanifycore.entities.ValidateIbanResponse;
import com.manuweb.ibanifycore.service.brandfetch.BrandfetchBankLogoService;
import com.manuweb.ibanifycore.service.openiban.OpenIbanMessageLocalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public final class IbanValidationService {

  private final IbanNormalizer normalizer;
  private final IbanValidationPort validationPort;
  private final BrandfetchBankLogoService brandfetchBankLogoService;
  private final OpenIbanMessageLocalizer openIbanMessageLocalizer;

  public IbanValidationService(
      IbanNormalizer normalizer,
      IbanValidationPort validationPort,
      BrandfetchBankLogoService brandfetchBankLogoService,
      OpenIbanMessageLocalizer openIbanMessageLocalizer) {
    this.normalizer = normalizer;
    this.validationPort = validationPort;
    this.brandfetchBankLogoService = brandfetchBankLogoService;
    this.openIbanMessageLocalizer = openIbanMessageLocalizer;
  }

  public ValidateIbanResponse validate(ValidateIbanRequest request, Locale locale) {
    String normalized = normalizer.normalizeToCanonical(request.input());
    OpenIbanApiResponse upstream = validationPort.validate(normalized);
    return toResponse(normalized, upstream, locale);
  }

  private ValidateIbanResponse toResponse(
      String normalized, OpenIbanApiResponse upstream, Locale locale) {
    OpenIbanBankData bank = upstream.bankData();
    String bankName = bank != null ? bank.name() : null;
    String bic = bank != null ? bank.bic() : null;
    String bankCode = bank != null ? bank.bankCode() : null;
    String bankCity = bank != null ? bank.city() : null;
    String bankZip = bank != null ? bank.zip() : null;
    List<String> rawMessages =
        upstream.messages() == null ? List.of() : List.copyOf(upstream.messages());
    List<String> messages = openIbanMessageLocalizer.localize(rawMessages, locale);
    String displayIban = IbanDisplayFormatter.format(normalized);
    String bankLogoUrl = brandfetchBankLogoService.logoUrlFor(bank);
    return new ValidateIbanResponse(
        normalized,
        displayIban,
        upstream.valid(),
        bankName,
        bic,
        bankCode,
        bankCity,
        bankZip,
        bankLogoUrl,
        messages);
  }
}
