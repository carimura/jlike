package com.pinealpha.demos.jlike;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.pinealpha.demos.jlike.twitter.*;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/")
@RequestScoped
public class RootResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String home() throws Exception {

    Jinjava jinjava = new Jinjava();
    Map<String, Object> context = Maps.newHashMap();

    context.put("content", buildPage());

    String template = Resources.toString(Resources.getResource("index.html"), Charsets.UTF_8);

    String renderedTemplate = jinjava.render(template, context);

    return renderedTemplate;
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