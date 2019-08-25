package com.pinealpha.demos.jlike;

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
    str += "Final Sentiment: " + getSentimentType() + "<br />";
    return str;
  }

  public static String getFinalSentimentHTML(Double score) {
    return switch((int)Math.round(score)) {
      case 0 -> "<b style='color: red;'>VERY NEGATIVE :((</b>";
      case 1 -> "<b style='color: darkred;'>NEGATIVE :(</b>";
      case 2 -> "<b style='color: blue;'>NEUTRAL :|</b>";
      case 3 -> "<b style='color: darkgreen;'>POSITIVE :)</b>";
      case 4 -> "<b style='color: green;'>VERY POSITIVE :))</b>";
      default -> "<b style=''>UNKNOWN SENTIMENT</b>";
    };
  }
}