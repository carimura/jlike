package com.pinealpha.demos.jlike.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TwitterService {
  public static Tweet[] search(String query, String maxResults, String fromDate, String toDate) throws Exception {
    HttpUrl.Builder builder = HttpUrl.parse("https://api.twitter.com/1.1/tweets/search/30day/dev.json").newBuilder();
    builder.addQueryParameter("query", query);
    builder.addQueryParameter("maxResults", maxResults);
    builder.addQueryParameter("fromDate", fromDate);
    builder.addQueryParameter("toDate", toDate);

    var respString = makeRequest(builder);
    ObjectMapper mapper = new ObjectMapper();
    SearchResult result = mapper.readValue(respString, SearchResult.class);
    Tweet[] tweets = result.results;
    return tweets;
  }

  public static Tweet getTweet(String tweetId) throws Exception {
    HttpUrl.Builder builder = HttpUrl.parse("https://api.twitter.com/1.1/statuses/show.json").newBuilder();
    builder.addQueryParameter("id", tweetId);
    var respString = makeRequest(builder);
    ObjectMapper mapper = new ObjectMapper();
    var tweet = mapper.readValue(respString, Tweet.class);

    return tweet;
  }

  public static Oembed getTweetEmbed(String url, String maxWidth) throws Exception {
    HttpUrl.Builder builder = HttpUrl.parse("https://api.twitter.com/1.1/statuses/oembed.json").newBuilder();
    builder.addQueryParameter("url", url);
    builder.addQueryParameter("maxWidth", maxWidth);
    var respString = makeRequest(builder);
    ObjectMapper mapper = new ObjectMapper();
    var oembed = mapper.readValue(respString, Oembed.class);
    return oembed;
  }

  public static Tweet[] getTimeline(String user, String count, String include_rts) throws Exception {
    HttpUrl.Builder builder = HttpUrl.parse("https://api.twitter.com/1.1/statuses/user_timeline.json").newBuilder();
    builder.addQueryParameter("screen_name", user);
    builder.addQueryParameter("count", count);
    builder.addQueryParameter("include_rts", include_rts);
    var respString = makeRequest(builder);
    ObjectMapper mapper = new ObjectMapper();
    Tweet[] tweets = mapper.readValue(respString, Tweet[].class);

    return tweets;
  }

  private static String makeRequest(HttpUrl.Builder builder) throws Exception {
    var client = new OkHttpClient();

    Request request = new Request.Builder()
        .url(builder.build().toString())
        .header("authorization", "Bearer " + System.getenv("TWITTER_BEARER"))
        .addHeader("content-type", "application/json")
        .build();

    try (var res = client.newCall(request).execute()) {
      if (!res.isSuccessful()) throw new IOException("\nUnexpected response: " + res + "\nBody: " + res.body().string());
      var respString = res.body().string();
      res.close();
      client.dispatcher().executorService().shutdown();
      client.connectionPool().evictAll();
      return respString;
    }
  }


}
