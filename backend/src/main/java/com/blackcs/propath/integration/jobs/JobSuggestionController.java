package com.blackcs.propath.integration.jobs;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobSuggestionController {

  private final RemoteOkClient remoteOkClient;

  public JobSuggestionController(RemoteOkClient remoteOkClient) {
    this.remoteOkClient = remoteOkClient;
  }

  @GetMapping("/remote")
  public ResponseEntity<List<RemoteOkJob>> listRemoteJobs(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "10") int limit) {
    List<RemoteOkJob> jobs = remoteOkClient.fetchJobs(keyword);
    if (limit > 0 && jobs.size() > limit) {
      jobs = jobs.subList(0, limit);
    }
    return ResponseEntity.ok(jobs);
  }
}
