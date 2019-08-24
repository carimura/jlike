# JLove - Sentiment Analysis in Java

[JavaNLP API Docs](https://nlp.stanford.edu/nlp/javadoc/javanlp/)

Test with: `1158420985915117579`

TODO:

- clean up UI
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

## Run

```
java -jar target/jlike.jar

--> http://localhost:8080/
--> http://localhost:8080/twitter/<username>
```


