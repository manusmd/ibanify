package com.manuweb.ibanifycore.service.openiban;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.manuweb.ibanifycore.entities.OpenIbanApiResponse;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class OpenIbanValidationAdapterTest {

  private WireMockServer wireMockServer;
  private OpenIbanValidationAdapter adapter;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockServer.start();
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);
    factory.setReadTimeout(5000);
    RestClient restClient = RestClient.builder().requestFactory(factory).build();
    OpenIbanProperties properties =
        new OpenIbanProperties(
            wireMockServer.baseUrl(),
            Duration.ofSeconds(2),
            Duration.ofSeconds(5),
            true,
            true);
    adapter = new OpenIbanValidationAdapter(restClient, properties);
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void returnsParsedBankData() {
    String iban = "DE89370400440532013000";
    wireMockServer.stubFor(
        get(urlPathEqualTo("/validate/" + iban))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                        {
                          "valid": true,
                          "messages": [],
                          "iban": "DE89370400440532013000",
                          "bankData": {
                            "bankCode": "37040044",
                            "name": "Test Bank",
                            "bic": "COBADEFFXXX"
                          },
                          "checkResults": {}
                        }
                        """)));

    OpenIbanApiResponse response = adapter.validate(iban);

    assertThat(response.valid()).isTrue();
    assertThat(response.bankData().name()).isEqualTo("Test Bank");
    assertThat(response.bankData().bic()).isEqualTo("COBADEFFXXX");
  }

  @Test
  void throwsOnUpstreamErrorStatus() {
    String iban = "DE89370400440532013000";
    wireMockServer.stubFor(
        get(urlPathEqualTo("/validate/" + iban)).willReturn(aResponse().withStatus(500)));

    assertThatThrownBy(() -> adapter.validate(iban)).isInstanceOf(IbanUpstreamException.class);
  }
}
