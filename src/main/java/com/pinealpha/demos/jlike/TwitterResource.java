package com.pinealpha.demos.jlike;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.pinealpha.demos.jlike.sentiment.*;
import com.pinealpha.demos.jlike.twitter.*;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Path("/twitter")
@RequestScoped
public class TwitterResource {

  Jinjava JJ = new Jinjava();

  public Map<String, Object> getContext() {
    Map<String, Object> context = Maps.newHashMap();
    return context;
  }

  @Path("/id/{id}")
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String processFromId(@PathParam("id") String id) throws Exception {
    System.out.println("----- ProcessFromId " + id + " -------");
    return "";
  }

  @Path("/{user}")
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String processFromUser(@PathParam("user") String user) throws Exception {
    System.out.println("----- ProcessFromUser " + user + " -------");

    var context = getContext();
    context.put("content", analyzeTweets("USER", user));

    String template = Resources.toString(Resources.getResource("twitter.html"), Charsets.UTF_8);

    String page = JJ.render(template, context);

    return page;
  }


  private static String analyzeTweets(String objectType, String obj) throws Exception {
    System.out.println("----- analyzeTweets " + objectType + " / " + obj + " -------");
    var sentimentAnalyzer = new Analyzer();
    sentimentAnalyzer.initialize();
    var resp = "";

    // GET TIMELINE OR SINGLE TWEET
    ArrayList<Tweet> tweets = new ArrayList<>();
    if (objectType == "ID") {
      Tweet temp = TwitterService.getTweet(obj);
      tweets.add(temp);
      resp += "<h1>Analysis of Tweet " + obj + "</h1><br />";
    } else {
      var arrayOfTweets = TwitterService.getTimeline(obj, "25", "false");
      for (Tweet tweet : arrayOfTweets) {
        tweets.add(tweet);
      }
      resp += "<h1>Analysis of @" + obj + " Tweets</h1><br />";
    }

    var format = DateTimeFormatter.ofPattern("yyyyMMdd0000");
    var fromDate = LocalDateTime.now().minusDays(30).format(format);
    var toDate = LocalDateTime.now().format(format);
    var query = "to:" + tweets.get(0).user.screen_name + " lang:en";

    // Get all tweets sent TO this user, for analysis in a bit.
    Tweet[] replies = TwitterService.search(query, "100", fromDate, toDate);

    resp += "<table style='border-spacing: 10px 20px; border-collapse: separate;'>";

    int totalRetweets = tweets.stream().mapToInt(t -> t.retweet_count).sum();
    int totalLikes = tweets.stream().mapToInt(t -> t.favorite_count).sum();
    int avgRetweets = totalRetweets / tweets.size();
    int avgLikes = totalLikes / tweets.size();

    // Now process each Tweet
    for (Tweet t : tweets) {
      Oembed oembed = TwitterService.getTweetEmbed(t.getUrl(), "400");

      String retweetAnalysis = getAnalysis(t.retweet_count, avgRetweets);
      String likeAnalysis = getAnalysis(t.favorite_count, avgLikes);

      resp += """
      <tr>
      <td>%s</td>
      <td style="vertical-align: top; padding-top: 15px;">
      Retweets: <b>%s</b> (%s)<br />
      Likes: <b>%s</b> (%s)<br />
      """.formatted(oembed.html, t.retweet_count, retweetAnalysis, t.favorite_count, likeAnalysis);

      var sentiments = new ArrayList<SentimentResult>();
      SentimentResult res;
      var delayedResp = "";

      // Iterate across all replies to outer tweet
      for (Tweet reply : replies) {

        // Skip if it's not a reply or isn't reply to parent. what a stupid API.
        if (reply.in_reply_to_status_id == null) {
          continue;
        }
        if (reply.in_reply_to_status_id.equals(t.id)) {
          res = sentimentAnalyzer.getSentimentResult(reply.text);
          sentiments.add(res);
          delayedResp +=
              """
              <p style="font-size: 11px;"><br />
              @%s - %s<br />
              %s
              </p>
              """.formatted(reply.user.screen_name, reply.text, res.getResultString());
        }
      }

      var sum = 0.0;
      for (SentimentResult r : sentiments) {
        sum += r.getSentimentScore();
      }
      var avg = sum / sentiments.size();
      if (sentiments.size() > 0) {

        resp += """
                Replies: <b>%s</b> <br />
                Average Sentiment: <b>%s</b> <font style="font-size: 10px;">(out of 4)</font>"<br />
                Sentiment Analysis: <b>%s</b>
                """.formatted(sentiments.size(),
            String.format("%.2f", avg),
            SentimentResult.getFinalSentimentHTML(avg));
      } else {
        resp += "No replies to calculate sentiment.";
      }

      resp += """
      <br />
      %s</td></tr>
      """.formatted(delayedResp);
    }

    return resp;
  }


  private static String getAnalysis(int current, int avg) {
    String analysis;
    double delta;
    if (current > avg) {
      delta = (double)(current - avg) / current * 100;
      analysis = "<font style=\"color: darkgreen;\">more than average by <b>" + (int)Math.round(delta) + "%</b></font>";
    } else if (current < avg) {
      delta = (double)(avg - current) / current * 100;
      analysis = "<font style=\"color: darkred;\">less than average by " + (int)Math.round(delta) + "%</b></font>";
    } else {
      analysis = "same as average";
    }
    return analysis;
  }


}




