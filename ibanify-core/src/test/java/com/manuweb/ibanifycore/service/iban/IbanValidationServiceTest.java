package com.manuweb.ibanifycore.service.iban;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manuweb.ibanifycore.entities.OpenIbanApiResponse;
import com.manuweb.ibanifycore.entities.OpenIbanBankData;
import com.manuweb.ibanifycore.entities.ValidateIbanRequest;
import com.manuweb.ibanifycore.entities.ValidateIbanResponse;
import com.manuweb.ibanifycore.service.brandfetch.BrandfetchBankLogoService;
import com.manuweb.ibanifycore.service.openiban.OpenIbanMessageLocalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IbanValidationServiceTest {

  @Mock private IbanValidationPort validationPort;

  @Mock private IbanNormalizer normalizer;

  @Mock private BrandfetchBankLogoService brandfetchBankLogoService;

  @Mock private OpenIbanMessageLocalizer openIbanMessageLocalizer;

  private IbanValidationService service;

  @BeforeEach
  void setUp() {
    service =
        new IbanValidationService(
            normalizer,
            validationPort,
            brandfetchBankLogoService,
            openIbanMessageLocalizer);
    when(openIbanMessageLocalizer.localize(anyList(), any())).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void mapsUpstreamToResponse() {
    when(normalizer.normalizeToCanonical("x")).thenReturn("DE89370400440532013000");
    OpenIbanBankData bankData =
        new OpenIbanBankData(
            "37040044", "Commerzbank", "60311", "Frankfurt", "COBADEFFXXX", null);
    when(validationPort.validate("DE89370400440532013000"))
        .thenReturn(
            new OpenIbanApiResponse(
                true,
                List.of("ok"),
                "DE89370400440532013000",
                bankData,
                Map.of("bankCode", true)));
    when(brandfetchBankLogoService.logoUrlFor(bankData))
        .thenReturn("https://cdn.brandfetch.io/commerzbank.de/icon.png?c=testid");

    ValidateIbanResponse response =
        service.validate(new ValidateIbanRequest("x"), Locale.ENGLISH);

    verify(brandfetchBankLogoService).logoUrlFor(bankData);
    assertThat(response.bankLogoUrl())
        .isEqualTo("https://cdn.brandfetch.io/commerzbank.de/icon.png?c=testid");
    assertThat(response.normalizedIban()).isEqualTo("DE89370400440532013000");
    assertThat(response.displayIban())
        .isEqualTo(IbanDisplayFormatter.format("DE89370400440532013000"));
    assertThat(response.valid()).isTrue();
    assertThat(response.bankName()).isEqualTo("Commerzbank");
    assertThat(response.bic()).isEqualTo("COBADEFFXXX");
    assertThat(response.bankCode()).isEqualTo("37040044");
    assertThat(response.bankCity()).isEqualTo("Frankfurt");
    assertThat(response.bankZip()).isEqualTo("60311");
    assertThat(response.messages()).containsExactly("ok");
  }
}
