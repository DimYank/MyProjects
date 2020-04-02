package async.task6;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RestService {

    private final static String LOGIN_REQUEST = "POST /api/session HTTP/1.1\r\n" +
            "Host: localhost:8888\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Type: application/json\r\n" +
            "Content-Length: 45\r\n\r\n" +
            "{\"name\": \"Admin\"," +
            "\"password\": \"administrator\"}";

    private final static String GET_USERS_REQUEST = "GET /api/users HTTP/1.1\r\n" +
            "Host: localhost:8888\r\n" +
            "Connection: keep-alive\r\n" +
            "Cookie: %s;\r\n\r\n";

    private final static String GET_FORUM_REQUEST = "GET /api/forums HTTP/1.1\r\n" +
            "Host: localhost:8888\r\n" +
            "Connection: keep-alive\r\n" +
            "Cookie: %s;\r\n\r\n";

    public void process(ChannelHandlerContext serverctx) throws Exception {
        Channel inboundChannel = serverctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 8888))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(Channel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpResponseDecoder());
                        p.addLast(new HttpObjectAggregator(100 * 1024));
                        p.addLast(new LoginHandler(serverctx));
                    }
                });
        b.connect();
    }


    private static class LoginHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

        private ChannelHandlerContext serverctx;

        public LoginHandler(ChannelHandlerContext serverctx) {
            this.serverctx = serverctx;
        }

        @Override
        public void channelActive(ChannelHandlerContext registerCtx) {
            System.out.println(LOGIN_REQUEST);
            registerCtx.writeAndFlush(Unpooled.copiedBuffer(LOGIN_REQUEST, CharsetUtil.UTF_8));
        }

        @Override
        public void channelRead0(ChannelHandlerContext registerCtx, FullHttpResponse in) {
            String cookie = in.headers().get("Set-Cookie");
            List<String> results = new ArrayList<>();

            Channel inboundChannel = serverctx.channel();
            Bootstrap b1 = new Bootstrap();
            b1.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost", 8888))
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) {
                            ChannelPipeline p1 = ch.pipeline();
                            p1.addLast(new HttpResponseDecoder());
                            p1.addLast(new HttpObjectAggregator(100 * 1024));
                            p1.addLast(new GetForumsHandler(serverctx, cookie, results));

                        }
                    });
            b1.connect();
            Bootstrap b2 = new Bootstrap();
            b2.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost", 8888))
            .handler(
                    new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) {
                            ChannelPipeline p2 = ch.pipeline();
                            p2.addLast(new HttpResponseDecoder());
                            p2.addLast(new HttpObjectAggregator(100 * 1024));
                            p2.addLast(new GetUsersHandler(serverctx, cookie, results));
                        }
                    });
            b2.connect();

        }

        @Override
        public void exceptionCaught(
                ChannelHandlerContext ctx, Throwable cause) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    OK,
                    Unpooled.copiedBuffer(cause.getStackTrace().toString(), CharsetUtil.UTF_8));
            response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            serverctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
            cause.printStackTrace();
            ctx.close();
        }

        private static class GetUsersHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

            private ChannelHandlerContext serverctx;
            private String cookie;
            private List<String> results;

            public GetUsersHandler(ChannelHandlerContext serverctx, String cookie, List<String> results) {
                this.serverctx = serverctx;
                this.cookie = cookie;
                this.results = results;
            }

            @Override
            public void channelActive(ChannelHandlerContext infoCtx) {
                System.out.println(String.format(GET_USERS_REQUEST, cookie));
                infoCtx.writeAndFlush(Unpooled.copiedBuffer(String.format(GET_USERS_REQUEST, cookie), CharsetUtil.UTF_8));
            }

            @Override
            public void channelRead0(ChannelHandlerContext infoCtx, FullHttpResponse in) {
                String result = in.content().toString(CharsetUtil.UTF_8);
                System.out.println("Users data: " + result);
                results.add(result);
                if (results.size() == 2) {
                    StringBuilder builder = new StringBuilder();
                    for (String str : results) {
                        builder.append(str);
                    }
                    String resStr = builder.toString();

                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HTTP_1_1,
                            OK,
                            Unpooled.copiedBuffer(resStr, CharsetUtil.UTF_8));
                    response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

                    serverctx.writeAndFlush(response)
                            .addListener(ChannelFutureListener.CLOSE);
                }
            }

            @Override
            public void exceptionCaught(
                    ChannelHandlerContext ctx, Throwable cause) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        OK,
                        Unpooled.copiedBuffer(cause.getStackTrace().toString(), CharsetUtil.UTF_8));
                response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                serverctx.writeAndFlush(response)
                        .addListener(ChannelFutureListener.CLOSE);
                cause.printStackTrace();
                ctx.close();
            }
        }

        private static class GetForumsHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

            private ChannelHandlerContext serverctx;
            private String cookie;
            private List<String> results;

            public GetForumsHandler(ChannelHandlerContext serverctx, String cookie, List<String> results) {
                this.serverctx = serverctx;
                this.cookie = cookie;
                this.results = results;
            }

            @Override
            public void channelActive(ChannelHandlerContext infoCtx) {
                System.out.println(String.format(GET_FORUM_REQUEST, cookie));
                infoCtx.writeAndFlush(Unpooled.copiedBuffer(String.format(GET_FORUM_REQUEST, cookie), CharsetUtil.UTF_8));
            }

            @Override
            public void channelRead0(ChannelHandlerContext infoCtx, FullHttpResponse in) {
                String result = in.content().toString(CharsetUtil.UTF_8);
                System.out.println("Forums data: " + result);
                results.add(result);

                if (results.size() == 2) {
                    StringBuilder builder = new StringBuilder();
                    for (String str : results) {
                        builder.append(str);
                    }
                    String resStr = builder.toString();

                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HTTP_1_1,
                            OK,
                            Unpooled.copiedBuffer(resStr, CharsetUtil.UTF_8));
                    response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

                    serverctx.writeAndFlush(response)
                            .addListener(ChannelFutureListener.CLOSE);
                }
            }

            @Override
            public void exceptionCaught(
                    ChannelHandlerContext ctx, Throwable cause) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        OK,
                        Unpooled.copiedBuffer(cause.getStackTrace().toString(), CharsetUtil.UTF_8));
                response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                serverctx.writeAndFlush(response)
                        .addListener(ChannelFutureListener.CLOSE);
                cause.printStackTrace();
                ctx.close();
            }
        }
    }
}
