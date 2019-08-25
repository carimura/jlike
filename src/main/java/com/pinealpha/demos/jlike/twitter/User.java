package com.pinealpha.demos.jlike.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
  public Long id;
  public String screen_name;
}
