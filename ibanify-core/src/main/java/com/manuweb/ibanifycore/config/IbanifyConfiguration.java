package com.manuweb.ibanifycore.config;

import com.manuweb.ibanifycore.service.brandfetch.BrandfetchProperties;
import com.manuweb.ibanifycore.service.openiban.OpenIbanProperties;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenIbanProperties.class, CorsProperties.class, BrandfetchProperties.class})
public final class IbanifyConfiguration {

  @Bean
  @Qualifier("openIbanRestClient")
  RestClient openIbanRestClient(OpenIbanProperties properties) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) properties.connectTimeout().toMillis());
    factory.setReadTimeout((int) properties.readTimeout().toMillis());
    return RestClient.builder().requestFactory(factory).build();
  }

  @Bean
  @Qualifier("brandfetchSearchRestClient")
  RestClient brandfetchSearchRestClient(BrandfetchProperties brandfetchProperties) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) brandfetchProperties.connectTimeout().toMillis());
    factory.setReadTimeout((int) brandfetchProperties.readTimeout().toMillis());
    return RestClient.builder()
        .requestFactory(factory)
        .baseUrl(brandfetchProperties.apiBaseUrl())
        .build();
  }

  @Bean
  WebMvcConfigurer ibanifyCorsConfigurer(CorsProperties corsProperties) {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = corsProperties.allowedOrigins();
        if (origins == null || origins.isEmpty()) {
          return;
        }
        registry
            .addMapping("/api/**")
            .allowedOrigins(origins.toArray(new String[0]))
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowedHeaders("*");
      }
    };
  }
}
