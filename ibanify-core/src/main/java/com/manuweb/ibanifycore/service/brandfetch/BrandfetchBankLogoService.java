package com.manuweb.ibanifycore.service.brandfetch;

import com.manuweb.ibanifycore.entities.BrandfetchSearchHit;
import com.manuweb.ibanifycore.entities.OpenIbanBankData;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public final class BrandfetchBankLogoService {

  private static final Logger log = LoggerFactory.getLogger(BrandfetchBankLogoService.class);

  private final BrandfetchProperties properties;
  private final RestClient brandfetchSearchRestClient;

  public BrandfetchBankLogoService(
      BrandfetchProperties properties,
      @Qualifier("brandfetchSearchRestClient") RestClient brandfetchSearchRestClient) {
    this.properties = properties;
    this.brandfetchSearchRestClient = brandfetchSearchRestClient;
  }

  public String logoUrlFor(OpenIbanBankData bank) {
    if (bank == null) {
      return null;
    }
    String clientId = properties.clientId();
    if (clientId == null || clientId.isBlank()) {
      return null;
    }

    String fromName = tryLogoFromBankNameSearch(bank, clientId);
    if (fromName != null) {
      return fromName;
    }

    String fromWebsite = hostFromWebsite(bank.website());
    if (fromWebsite != null) {
      return buildLogoApiUrl(fromWebsite, clientId);
    }

    return null;
  }

  private String tryLogoFromBankNameSearch(OpenIbanBankData bank, String clientId) {
    String rawName = bank.name();
    if (rawName == null || rawName.isBlank() || rawName.strip().length() < 2) {
      return null;
    }
    String q = rawName.strip();
    try {
      List<BrandfetchSearchHit> hits =
          brandfetchSearchRestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path("/v2/search/{name}")
                          .queryParam("c", clientId)
                          .build(q))
              .retrieve()
              .body(new ParameterizedTypeReference<List<BrandfetchSearchHit>>() {});
      if (hits == null || hits.isEmpty()) {
        return null;
      }
      BrandfetchSearchHit hit = hits.getFirst();
      if (hit.icon() != null && !hit.icon().isBlank()) {
        return hit.icon().strip();
      }
      if (hit.domain() != null && !hit.domain().isBlank()) {
        String host = hostFromWebsite(hit.domain());
        if (host != null) {
          return buildLogoApiUrl(host, clientId);
        }
        return buildLogoApiUrl(normalizeRegistrableDomain(hit.domain()), clientId);
      }
      return null;
    } catch (RestClientException e) {
      log.warn("Brandfetch search request failed");
      return null;
    }
  }

  private static String hostFromWebsite(String website) {
    if (website == null || website.isBlank()) {
      return null;
    }
    String trimmed = website.strip();
    try {
      URI uri =
          trimmed.contains("://")
              ? URI.create(trimmed)
              : URI.create("https://" + trimmed);
      String host = uri.getHost();
      if (host == null || host.isBlank()) {
        return null;
      }
      return normalizeRegistrableDomain(host);
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }

  private static String normalizeRegistrableDomain(String domain) {
    String d = domain.strip().toLowerCase(Locale.ROOT);
    if (d.startsWith("www.")) {
      d = d.substring(4);
    }
    return d;
  }

  private String buildLogoApiUrl(String domain, String clientId) {
    return UriComponentsBuilder.fromUriString(properties.cdnHost())
        .pathSegment(domain)
        .pathSegment("icon.png")
        .queryParam("c", clientId)
        .build()
        .encode()
        .toUriString();
  }
}
