package com.blackcs.propath.integration.jobs;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class JobsClientConfig {

  @Bean
  public WebClient remoteOkWebClient() {
    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
            .responseTimeout(Duration.ofSeconds(10));
    return WebClient.builder()
        .baseUrl("https://remoteok.com")
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(c -> c.defaultCodecs().maxInMemorySize(8 * 1024 * 1024))
        .defaultHeader(
            HttpHeaders.USER_AGENT,
            "ProPath/0.1 (student project; contact: lovinson.dieujuste.tech@gmail.com)")
        .defaultHeader(HttpHeaders.ACCEPT, "application/json")
        .build();
  }
}
