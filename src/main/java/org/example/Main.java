package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {


            // Define server port
            int port = 8080;


            // Create and configure server
            ServerConfig serverConfig = new ServerConfig(port);
            ServerInitializer serverInitializer = new ServerInitializer();
          //  System.setProperty("io.netty.eventLoopThreads","12");
            // Start the server
            serverInitializer.startServer(serverConfig.getPort());
        } catch (InterruptedException e) {
            logger.error("Server interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
