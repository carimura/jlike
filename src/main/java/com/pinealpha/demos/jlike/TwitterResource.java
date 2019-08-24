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

@Path("/twitter")
@RequestScoped
public class TwitterResource {

  private static final String TEMPLATE =
      """
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
      """;

  @Path("/id/{id}")
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String processFromId(@PathParam("id") String id) throws Exception {
    System.out.println("----- ProcessFromId " + id + " -------");
    return TEMPLATE.formatted(analyzeTweets("ID", id));
  }

  @Path("/{user}")
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String processFromUser(@PathParam("user") String user) throws Exception {
    System.out.println("----- ProcessFromUser " + user + " -------");
    return TEMPLATE.formatted(analyzeTweets("USER", user));
  }


  private static String analyzeTweets(String objectType, String obj) throws Exception {
    System.out.println("----- analyzeTweets " + objectType + " / " + obj + " -------");
    var twitter = setupTwitter();
    var sentimentAnalyzer = new Analyzer();
    sentimentAnalyzer.initialize();
    var resp = "";

    List<Status> tweets = new ArrayList<>();

    if (objectType == "ID") {
      Status temp = twitter.showStatus(Long.parseLong(obj));
      tweets.add(temp);
      resp += "<h1>Analysis of Tweet " + obj + "</h1><br />";
    } else {
      tweets = twitter.getUserTimeline(obj, new Paging(1, 20));
      resp += "<h1>Analysis of @" + obj + " Tweets</h1><br />";
    }

    System.out.println("Analyzing " + tweets.size() + " tweets for " + obj);

    // FOR EACH TWEET
    for (Status tweet : tweets) {
      Status t = twitter.showStatus(tweet.getId());

      var embedReq = new OEmbedRequest(t.getId(), "https://twitter.com/" + obj + "/status/" + t.getId());
      embedReq.setMaxWidth(500);
      var embed = twitter.getOEmbed(embedReq);
      resp += embed.getHtml();

      resp += """
      Retweets: <b>%s</b> <br />
      Likes: <b>%s</b> <br />
      """.formatted(t.getRetweetCount(), t.getFavoriteCount());

      // Search all Tweets to:user of parent tweet since the parent tweet was sent
      var qstring = "to:@" + t.getUser().getScreenName();
      System.out.println("qstring --> " + qstring);
      var q = new Query(qstring);
      q.setSinceId(t.getId());
      q.setCount(200);
      var queryResult = twitter.search(q);
      System.out.println("queryResult.getQuery() --> " + queryResult.getQuery() + " / " + q.getSinceId());
      System.out.println(queryResult.toString());
      var statuses = queryResult.getTweets();
      System.out.println("statuses.size() --> " + statuses.size());



      var sentiments = new ArrayList<SentimentResult>();
      SentimentResult res;
      var delayedResp = "";

      // Iterate across all replies to outer tweet
      for (Status status : statuses) {

        // Skip if isn't a reply to our parent tweet. what a joke of an API.
        if (status.getInReplyToStatusId() != tweet.getId()) {
          System.out.println("skip " + tweet.getId() + " ");
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




