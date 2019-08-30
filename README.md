# JLove - Sentiment Analysis in Java

[JavaNLP API Docs](https://nlp.stanford.edu/nlp/javadoc/javanlp/)

Test with: `1158420985915117579`

TODO:

- simple html template would be great
- create bearer token if doesn't exist
- Influencer score of retweeters 
- add Fibers
- add jlink and Docker?
- sentiments seem low...
- train model -- feed info back to model.
- to include models or not to include them, probably not. too big. maybe
  download at Docker build time and utilize its caching
  



## Build

```
mvn package
```

## Setup

```
❯ curl -u "$TWITTER_CONS_KEY:$TWITTER_CONS_SECRET" \
  --data 'grant_type=client_credentials' \
  'https://api.twitter.com/oauth2/token'

❯ export TWITTER_BEARER=<result from above>
```

## Run


```
java --enable-preview -jar target/jlike.jar

--> http://localhost:8080/
--> http://localhost:8080/twitter/<username>
```


