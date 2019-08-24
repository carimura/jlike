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

@Path("/twitter/{user}")
@RequestScoped
public class TwitterResource {
  @PathParam("user")
  String user;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String output() throws Exception {
    return """
    <!DOCTYPE html>
    <html>
    <header>
      <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
      <style>
        html { margin: 20px; }
        h2 { font-size: 1.5rem; }
        a { text-decoration: underline; }
        hr { border-top: 1px solid rgba(0,0,0,.4) }
        </style>
      </header>
      <body>
        %s
      </body>
    </html>
    """.formatted(analyzeTweets(user));
  }


  private static String analyzeTweets(String user) throws Exception {
    var twitter = setupTwitter();
    var sentimentAnalyzer = new Analyzer();
    sentimentAnalyzer.initialize();
    var resp = "<h1>Analysis of @" + user + " Tweets</h1><br />";

    ResponseList<Status> tweets = twitter.getUserTimeline(user, new Paging(1, 20));
//    Status temp = twitter.showStatus(Long.parseLong("1148226127384604673"));
//    var tweets = new ArrayList<Status>();
//    tweets.add(temp);
    System.out.println("found " + tweets.size() + " tweets for user " + user);

    // FOR EACH TWEET
    for (Status tweet : tweets) {
      Status t = twitter.showStatus(tweet.getId());

      var embedReq = new OEmbedRequest(t.getId(), "https://twitter.com/"+user+"/status/"+t.getId());
      embedReq.setMaxWidth(500);
      var embed = twitter.getOEmbed(embedReq);
      resp += embed.getHtml();

      resp += """
      Retweets: <b>%s</b> <br />
      Likes: <b>%s</b> <br />
      """.formatted(t.getRetweetCount(), t.getFavoriteCount());

      // Get all mentions of user since the tweet in question
      var q = new Query("to:@" + user);
      q.setSinceId(t.getId());
      q.setCount(20);
      var queryResult = twitter.search(q);
      var statuses = queryResult.getTweets();

      var sentiments = new ArrayList<SentimentResult>();
      SentimentResult res;
      var delayedResp = "";

      // ALL MENTIONS OF THE OUTER TWEET
      for (Status status : statuses) {
        if (status.getInReplyToStatusId() != tweet.getId()) {
          continue;
        }
        res = sentimentAnalyzer.getSentimentResult(status.getText());
        sentiments.add(res);
        delayedResp += """
        <p style="font-size: 11px;"><br />
        @%s - %s<br />
        %s
        </p>
        """.formatted(status.getUser().getScreenName(), status.getText(), res.getResultString());
      }

      System.out.println("Found " + sentiments.size() + " sentiments");

      var sum = 0.0;
      for (SentimentResult r : sentiments) {
        sum += r.getSentimentScore();
      }
      var avg = sum / sentiments.size();
      if (sentiments.size() > 0) {
        resp += """
        Replies: <b>%s</b><br />
        Average Sentiment: <b>%s</b> <font style="font-size: 10px;">(out of 4)</font>"<br />
        Sentiment Analysis: <b>%s</b>
        """.formatted(sentiments.size(), String.format("%.2f", avg), SentimentResult.getFinalSentimentHTML(avg));
      } else {
        resp += "Not enough data to calculate sentiment.";
      }

      resp += """
      <br />%s<br /><hr />
      """.formatted(delayedResp);
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




