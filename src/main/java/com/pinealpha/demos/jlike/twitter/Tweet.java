package com.pinealpha.demos.jlike.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
  public Long id;
  public String text;
  public User user;
  public int favorite_count;
  public int retweet_count;
  public Long in_reply_to_status_id;

  public String getUrl() {
    var url = "https://twitter.com/" + user.screen_name + "/status/" + id;
    System.out.println(url);
    return url;
  }
}