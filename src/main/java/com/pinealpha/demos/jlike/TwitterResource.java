package com.pinealpha.demos.jlike;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

@Path("/twitter/{user}")
@RequestScoped
public class TwitterResource {
  @PathParam("user") String user;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String output() throws Exception {
    var resp = "<html><header></header><body>";

    resp += analyzeTweets(user);
    resp += "</body></html>";
    return resp;
  }


  static String analyzeTweets(String user) throws Exception {
    var twitter = setupTwitter();
    var sentimentAnalyzer = new Analyzer();
    sentimentAnalyzer.initialize();
    var resp = "<h1>Sentiment of @" + user + " Tweets</h2><br />";

    ResponseList<Status> tweets = twitter.getUserTimeline(user, new Paging(1, 30));
    System.out.println("found " + tweets.size() + " tweets for user " + user);

    // FOR EACH TWEET
    for (Status tweet : tweets) {
      Status t = twitter.showStatus(tweet.getId());
      //var embedReq = new OEmbedRequest(tweetId, "https://twitter.com/chadarimura/status/1158420985915117579");
      //var embed = twitter.getOEmbed(embedReq);
      //resp += embed.getHtml();
      resp += "<h2>" + t.getText() + "</h2>";

      // Get all mentions of user since the tweet in question
      var q = new Query("to:@" + user);
      q.setSinceId(t.getId());
      q.setCount(20);
      var queryResult = twitter.search(q);
      var statuses = queryResult.getTweets();

      var sentiments = new ArrayList<SentimentResult>();
      SentimentResult res;

      // TRYING TO GET ALL REPLIES TO TWEET AND RUN ANALYSIS
      for (Status status : statuses) {
        if (status.getInReplyToStatusId() != tweet.getId()) {
          continue;
        }
        res = sentimentAnalyzer.getSentimentResult(status.getText());
        sentiments.add(res);
        resp += "<p style=\"font-size: 11px;\"><br />mention: @" + status.getUser().getScreenName() + " - " + status.getText() + "<br />";
        resp += res.getResultString();
      }
      System.out.println("Found " + sentiments.size() + " sentiments");
      var sum = 0.0;
      for (SentimentResult r : sentiments) {
        sum += r.getSentimentScore();
      }
      var avg = sum / sentiments.size();
      if (sentiments.size() > 0) {
        resp += "Based on " + sentiments.size() + " replies, the average sentiment out of 4 is " + String.format("%.2f",avg);
      } else {
        resp += "Not enough data to calculate sentiment.";
      }

      resp += "<br /><br /><hr />";
    }

    return resp;
  }


  private static Twitter setupTwitter() {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
        .setOAuthConsumerKey(System.getenv("TWITTER_CONS_KEY"))
        .setOAuthConsumerSecret(System.getenv("TWITTER_CONS_SECRET"))
        .setOAuthAccessToken(System.getenv("TWITTER_TOKEN"))
        .setOAuthAccessTokenSecret(System.getenv("TWITTER_TOKEN_SECRET"));
    TwitterFactory tf = new TwitterFactory(cb.build());
    return tf.getInstance();
  }


}




