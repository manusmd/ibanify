package com.manuweb.ibanifycore.service.brandfetch;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.manuweb.ibanifycore.entities.OpenIbanBankData;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class BrandfetchBankLogoServiceTest {

  private WireMockServer wireMockServer;
  private BrandfetchBankLogoService service;

  private static BrandfetchProperties props(String clientId) {
    return new BrandfetchProperties(
        clientId,
        "http://unused.example",
        Duration.ofSeconds(2),
        Duration.ofSeconds(5),
        "https://cdn.brandfetch.io");
  }

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockServer.start();
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);
    factory.setReadTimeout(5000);
    RestClient searchClient =
        RestClient.builder()
            .requestFactory(factory)
            .baseUrl(wireMockServer.baseUrl())
            .build();
    service = new BrandfetchBankLogoService(props("myclient"), searchClient);
    wireMockServer.stubFor(
        get(urlPathMatching("/v2/search/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]")));
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void returnsNullWhenClientIdBlank() {
    var local =
        new BrandfetchBankLogoService(
            props(""),
            RestClient.builder().baseUrl(wireMockServer.baseUrl()).build());
    var bank =
        new OpenIbanBankData("37040044", "Commerzbank", null, null, "COBADEFFXXX", null);
    assertThat(local.logoUrlFor(bank)).isNull();
  }

  @Test
  void usesSearchIconUrlWhenPresent() {
    wireMockServer.resetAll();
    wireMockServer.stubFor(
        get(urlPathMatching("/v2/search/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                        [{"icon":"https://cdn.brandfetch.io/x/icon.png?c=z","name":"Commerzbank","domain":"commerzbank.de"}]
                        """)));
    var bank =
        new OpenIbanBankData("37040044", "Commerzbank", null, null, "COBADEFFXXX", null);
    assertThat(service.logoUrlFor(bank))
        .isEqualTo("https://cdn.brandfetch.io/x/icon.png?c=z");
  }

  @Test
  void usesSearchDomainForLogoWhenIconMissing() {
    wireMockServer.resetAll();
    wireMockServer.stubFor(
        get(urlPathMatching("/v2/search/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                        [{"icon":null,"name":"Commerzbank","domain":"commerzbank.de"}]
                        """)));
    var bank =
        new OpenIbanBankData("37040044", "Commerzbank", null, null, "COBADEFFXXX", null);
    assertThat(service.logoUrlFor(bank))
        .isEqualTo("https://cdn.brandfetch.io/commerzbank.de/icon.png?c=myclient");
  }

  @Test
  void prefersBankNameSearchOverWebsite() {
    wireMockServer.resetAll();
    wireMockServer.stubFor(
        get(urlPathMatching("/v2/search/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                        [{"icon":"https://cdn.brandfetch.io/from-search/icon.png?c=z","domain":"other.de"}]
                        """)));
    var withWebsiteAndBic =
        new BrandfetchBankLogoService(
            props("myclient"),
            RestClient.builder().baseUrl(wireMockServer.baseUrl()).build());
    var bank =
        new OpenIbanBankData(
            "37040044",
            "Commerzbank",
            null,
            null,
            "COBADEFFXXX",
            "https://www.from-website.de/foo");
    assertThat(withWebsiteAndBic.logoUrlFor(bank))
        .isEqualTo("https://cdn.brandfetch.io/from-search/icon.png?c=z");
  }

  @Test
  void fallsBackToWebsiteWhenSearchEmpty() {
    var withWebsite =
        new BrandfetchBankLogoService(
            props("x"),
            RestClient.builder().baseUrl(wireMockServer.baseUrl()).build());
    var bank =
        new OpenIbanBankData(
            "37040044", "Commerzbank", null, null, "COBADEFFXXX", "https://www.commerzbank.de/foo");
    assertThat(withWebsite.logoUrlFor(bank))
        .isEqualTo("https://cdn.brandfetch.io/commerzbank.de/icon.png?c=x");
  }

  @Test
  void returnsNullWhenNoHitAndNoFallback() {
    var noMap =
        new BrandfetchBankLogoService(
            props("x"),
            RestClient.builder().baseUrl(wireMockServer.baseUrl()).build());
    var bank =
        new OpenIbanBankData("37040044", "Unknown", null, null, "XXXXXXXXXXX", null);
    assertThat(noMap.logoUrlFor(bank)).isNull();
  }
}
