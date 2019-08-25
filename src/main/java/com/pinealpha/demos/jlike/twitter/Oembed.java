package com.pinealpha.demos.jlike.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Oembed {
  public String url;
  public String html;
}
