package com.pinealpha.demos.jlike;

import com.pinealpha.demos.jlike.twitter.*;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class RootResource {

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

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String home() throws Exception {
    return TEMPLATE.formatted(buildPage());
  }


  private static String buildPage() throws Exception {
    var resp = "";
    Tweet t = TwitterService.getTweet("1158420985915117579");
    System.out.println("URL ---> " + t.getUrl());

    Oembed oembed = TwitterService.getTweetEmbed("https://twitter.com/chadarimura/status/1158420985915117579", "500");

    resp += oembed.html;

    return resp;
  }


}