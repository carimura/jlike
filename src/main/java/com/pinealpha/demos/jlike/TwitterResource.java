package com.pinealpha.demos.jlike;

import com.pinealpha.demos.jlike.twitter.*;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;

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
      Tweet[] arrayOfTweets = TwitterService.getTimeline(obj, "10");
      for (Tweet tweet : arrayOfTweets) {
        tweets.add(tweet);
      }
      resp += "<h1>Analysis of @" + obj + " Tweets</h1><br />";
    }

    // Get all tweets sent TO this user, for analysis in a bit.
    Tweet[] replies = TwitterService.search(
        "to:" + tweets.get(0).user.screen_name + " lang:en",
        "100",
        "201908010100",
        "201908241000"
    );

    // FOR EACH TWEET
    for (Tweet t : tweets) {
      Oembed oembed = TwitterService.getTweetEmbed(t.getUrl(), "500");
      resp += oembed.html;

      resp += """
      Retweets: <b>%s</b> <br />
      Likes: <b>%s</b> <br />
      """.formatted(t.retweet_count, t.favorite_count);

      var sentiments = new ArrayList<SentimentResult>();
      SentimentResult res;
      var delayedResp = "";

      // Iterate across all replies to outer tweet
      for (Tweet reply : replies) {
        // Skip if isn't a reply to our parent tweet. what a joke of an API.
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

}




