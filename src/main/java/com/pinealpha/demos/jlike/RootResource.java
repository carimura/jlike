package com.pinealpha.demos.jlike;

import java.util.Collections;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class RootResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String message() {
    return "Sentiment Analyzer. Go to /twitter/<user> or /twitter/id/<id>";
  }
}