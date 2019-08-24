package com.pinealpha.demos.jlike;

import twitter4j.Status;

public class SentimentResult {
  double sentimentScore;
  String sentimentType;
  Classification sentimentClass;

  public double getSentiment() {
    return sentimentScore;
  }

  public double getSentimentScore() {
    return sentimentScore;
  }

  public void setSentimentScore(double sentimentScore) {
    this.sentimentScore = sentimentScore;
  }
  public String getSentimentType() {
    return sentimentType;
  }
  public void setSentimentType(String sentimentType) {
    this.sentimentType = sentimentType;
  }
  public Classification getSentimentClass() {
    return sentimentClass;
  }
  public void setSentimentClass(Classification sentimentClass) {
    this.sentimentClass = sentimentClass;
  }

  public String getResultString() {
    var str = "Very positive: " + getSentimentClass().getVeryPositive() + "% <br />";
    str += "Positive: " + getSentimentClass().getPositive() + "% <br />";
    str += "Neutral: " + getSentimentClass().getNeutral() + "% <br />";
    str += "Negative: " + getSentimentClass().getNegative() + "% <br />";
    str += "Very negative: " + getSentimentClass().getVeryNegative() + "% <br />";
    str += "Final Score: " + getSentimentScore() + " <br />";
    str += "Final Sentiment: " + getSentimentType() + "<br /></p>";
    return str;
  }
}