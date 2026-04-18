package com.blackcs.propath.integration.jobs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteOkJob(
    String id,
    String company,
    String position,
    List<String> tags,
    String url,
    @JsonProperty("date") String date,
    @JsonProperty("location") String location) {}
