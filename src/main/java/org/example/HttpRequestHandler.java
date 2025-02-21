package org.example;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        logger.info("Received request: {}", request.uri());

        String name = "Guest"; // Default name
        try {
            URI uri = new URI(request.uri());
            String query = uri.getQuery(); // Extract query parameters

            if (query != null && query.startsWith("name=")) {
                name = query.split("=")[1];
            }
        } catch (Exception e) {
            logger.error("Error parsing URI: {}", e.getMessage());
        }

        String responseContent = generateHtmlPage(name);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                io.netty.buffer.Unpooled.copiedBuffer(responseContent, StandardCharsets.UTF_8)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String generateHtmlPage(String name) {
        return "<html>" +
                "<head><title>Netty Server</title></head>" +
                "<body>" +
                "<h1>Welcome, " + name + "!</h1>" +
                "<p>Type a name in the URL: <strong>?name=YourName</strong></p>" +
                "<p>Example: <a href='http://localhost:8080?name=John'>http://localhost:8080?name=John</a></p>" +
                "</body></html>";
    }
}
