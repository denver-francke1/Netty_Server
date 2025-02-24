package org.example;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
    public static void main(String[] args)  throws InterruptedException {
        int port = 8080;
        ServerConfig serverConfig = new ServerConfig(port);
        ServerInitializer serverInitializer = new ServerInitializer();
        serverInitializer.startServer(serverConfig);

    }
}
