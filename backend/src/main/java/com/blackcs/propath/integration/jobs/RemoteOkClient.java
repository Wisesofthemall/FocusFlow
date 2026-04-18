package com.blackcs.propath.integration.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RemoteOkClient {

  private static final Logger log = LoggerFactory.getLogger(RemoteOkClient.class);
  private static final Duration CACHE_TTL = Duration.ofMinutes(10);
  private static final int MAX_RESULTS = 25;

  private final WebClient webClient;
  private final Map<String, CachedResult> cache = new ConcurrentHashMap<>();

  public RemoteOkClient(WebClient remoteOkWebClient) {
    this.webClient = remoteOkWebClient;
  }

  public List<RemoteOkJob> fetchJobs(String keyword) {
    String key = keyword == null ? "" : keyword.trim().toLowerCase();
    CachedResult cached = cache.get(key);
    if (cached != null && cached.fetchedAt.plus(CACHE_TTL).isAfter(Instant.now())) {
      return cached.jobs;
    }
    List<RemoteOkJob> fresh = fetchFromRemote(key);
    cache.put(key, new CachedResult(fresh, Instant.now()));
    return fresh;
  }

  private List<RemoteOkJob> fetchFromRemote(String keyword) {
    try {
      List<RemoteOkJob> raw =
          webClient
              .get()
              .uri("/api")
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<List<RemoteOkJob>>() {})
              .timeout(Duration.ofSeconds(10))
              .onErrorResume(
                  e -> {
                    log.warn("RemoteOK fetch failed: {}", e.toString());
                    return reactor.core.publisher.Mono.just(List.of());
                  })
              .block();
      if (raw == null || raw.isEmpty()) {
        return List.of();
      }
      List<RemoteOkJob> filtered = new ArrayList<>();
      boolean first = true;
      for (RemoteOkJob job : raw) {
        if (first) {
          first = false;
          continue;
        }
        if (job.position() == null && job.company() == null) {
          continue;
        }
        if (keyword.isBlank() || matches(job, keyword)) {
          filtered.add(job);
          if (filtered.size() >= MAX_RESULTS) {
            break;
          }
        }
      }
      return filtered;
    } catch (Exception e) {
      log.warn("RemoteOK fetch raised {}; returning empty list", e.toString());
      return List.of();
    }
  }

  private boolean matches(RemoteOkJob job, String keyword) {
    String k = keyword.toLowerCase();
    if (job.position() != null && job.position().toLowerCase().contains(k)) return true;
    if (job.company() != null && job.company().toLowerCase().contains(k)) return true;
    if (job.tags() != null) {
      for (String tag : job.tags()) {
        if (tag != null && tag.toLowerCase().contains(k)) return true;
      }
    }
    return false;
  }

  private record CachedResult(List<RemoteOkJob> jobs, Instant fetchedAt) {}
}
