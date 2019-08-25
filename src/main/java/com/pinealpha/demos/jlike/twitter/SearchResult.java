package com.pinealpha.demos.jlike.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
  public Tweet[] results;
}
