package com.manuweb.ibanifycore.service.openiban;

import com.manuweb.ibanifycore.entities.OpenIbanApiResponse;
import com.manuweb.ibanifycore.service.iban.IbanValidationPort;
import java.net.URI;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public final class OpenIbanValidationAdapter implements IbanValidationPort {

  private final RestClient openIbanRestClient;
  private final OpenIbanProperties properties;

  public OpenIbanValidationAdapter(
      @Qualifier("openIbanRestClient") RestClient openIbanRestClient,
      OpenIbanProperties properties) {
    this.openIbanRestClient = openIbanRestClient;
    this.properties = properties;
  }

  @Override
  public OpenIbanApiResponse validate(String normalizedIban) {
    URI uri =
        UriComponentsBuilder.fromUriString(properties.baseUrl())
            .pathSegment("validate", normalizedIban)
            .queryParam("getBIC", properties.getBic())
            .queryParam("validateBankCode", properties.validateBankCode())
            .build()
            .encode()
            .toUri();
    try {
      OpenIbanApiResponse body =
          openIbanRestClient
              .get()
              .uri(uri)
              .retrieve()
              .onStatus(
                  HttpStatusCode::isError,
                  (request, response) -> {
                    throw new IbanUpstreamException(
                        "iban.error.upstream.http_status",
                        response.getStatusCode().value());
                  })
              .body(OpenIbanApiResponse.class);
      if (body == null) {
        throw new IbanUpstreamException("iban.error.upstream.empty_body");
      }
      return body;
    } catch (RestClientException e) {
      throw new IbanUpstreamException("iban.error.upstream.unavailable", e);
    }
  }
}
