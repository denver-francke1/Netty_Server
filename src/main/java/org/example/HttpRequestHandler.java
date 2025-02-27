package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        String uri = request.uri();
        logger.info("Received HTTP request: {} {}", request.method(), uri);

        // Ignore /favicon.ico requests
        if (uri.equals("/favicon.ico")) {
            logger.info("Ignoring favicon request");
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        // Log headers
        HttpHeaders headers = request.headers();
        logger.debug("Request Headers: {}", headers);

        // Log query parameters (if any)
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();
        if (!parameters.isEmpty()) {
            logger.info("Query parameters: {}", parameters);
        }

        // Serve HTML file for root request
        String name = "Guest";  // Default name
        if (parameters.containsKey("name")) {
            name = parameters.get("name").get(0);
        }

        // Log name extracted from query parameters
        logger.info("Name parameter: {}", name);

        // Load index.html from the public directory
        String filePath = System.getProperty("user.dir") + "/public/index.html";
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String content = new String(fileBytes, StandardCharsets.UTF_8);

        // Replace placeholder with actual name
        content = content.replace("{{name}}", name);

        // Log the content being sent
        logger.debug("Sending HTML content: \n{}", content);

        // Create and send the response
        ByteBuf responseContent = Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8));
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, responseContent);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseContent.readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
