package com.pinealpha.demos.jlike;

import java.io.IOException;
import java.util.logging.LogManager;

import io.helidon.microprofile.server.Server;

public final class Main {

  private Main() {
  }

  /**
   * Application main entry point.
   *
   * @param args command line arguments
   * @throws IOException if there are problems reading logging properties
   */
  public static void main(final String[] args) throws IOException {
    setupLogging();

    Server server = startServer();

    System.out.println("Running on http://localhost:" + server.port());
  }

  /**
   * Start the server.
   *
   * @return the created {@link Server} instance
   */
  static Server startServer() {
    // Server will automatically pick up configuration from
    // microprofile-config.properties
    // and Application classes annotated as @ApplicationScoped
    return Server.create().start();
  }

  private static void setupLogging() throws IOException {
    LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
  }
}
