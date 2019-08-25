# JLove - Sentiment Analysis in Java

[JavaNLP API Docs](https://nlp.stanford.edu/nlp/javadoc/javanlp/)

Test with: `1158420985915117579`

TODO:

- get rid of old twitter code
- dates are hardcoded. need to fix that.
- add Fibers
- add jlink and Docker?
- sentiments seem low...
- show details of individual score and why it was scored that way.
- train model -- feed info back to model.
- to include models or not to include them, probably not. too big. maybe
  download at Docker build time and utilize its caching
  



## Build

```
mvn package
```

## Setup

`System.getenv("TWITTER_BEARER")`

## Run


```
java --enable-preview -jar target/jlike.jar

--> http://localhost:8080/
--> http://localhost:8080/twitter/<username>
```


