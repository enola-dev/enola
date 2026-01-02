/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.web.netty;

import dev.enola.common.concurrent.Executors;
import dev.enola.web.WebHandlers;
import dev.enola.web.WebServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * HTTP {@link WebServer} API implementation based on <a href="https://netty.io">Netty</a>.
 *
 * <p>Currently supports only HTTP 1.1, without TLS (SSL, https:). HTTP/2 support could be added,
 * but requires TLS (not "technically" by the standard spec, but "practically" by all real-world
 * browsers). TLS support is possible, but self-signed certs and what not are a PITA. The general
 * idea is that a real-world Enola.dev server would anyway run behind a proxy server which does
 * HTTP/2, compression, authentication, etc.
 *
 * <p>This code is based on <a
 * href="https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/">Netty's
 * Examples.</a>
 *
 * <p>This is the successor of a first implementation that was based on <code>
 * com.sun.net.httpserver.HttpServer</code>, which was abandoned because <a
 * href="https://github.com/enola-dev/enola/issues/849">it could not support <code>
 * ThreadLocal</code></a>.
 */
public class NettyHttpServer implements WebServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpServer.class);

    private final WebHandlers handlers;
    private InetSocketAddress inetSocketAddress;
    private final NioEventLoopGroup connectionsGroup;
    private final EventLoopGroup handlerGroup;

    public NettyHttpServer(int port, WebHandlers handlers) {
        this.inetSocketAddress = new InetSocketAddress(port);
        this.handlers = handlers;

        // Accepts connections
        var connectExecutor = Executors.newSingleThreadExecutor("NettyHttpServer-Connect", LOG);
        connectionsGroup = new NioEventLoopGroup(1, connectExecutor);

        // Handles I/O for connected clients
        // TODO Use newListeningFixedThreadPool() to avoid unbounded growth? But how to choose size?
        var handlerExecutor = Executors.newCachedThreadPool("NettyHttpServer-Handler", LOG);
        handlerGroup = new NioEventLoopGroup(0, handlerExecutor);
    }

    @Override
    public void start() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        // TODO Configure additional ChannelOption ?

        b.group(connectionsGroup, handlerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new OurChannelInitializer(handlers));

        inetSocketAddress =
                (InetSocketAddress) b.bind(inetSocketAddress).sync().channel().localAddress();
    }

    @Override
    public InetSocketAddress getInetAddress() {
        return inetSocketAddress;
    }

    @Override
    public void close() {
        try {
            connectionsGroup.shutdownGracefully().get();
            handlerGroup.shutdownGracefully().get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Failed to close()", e);
        }
    }

    private static class OurChannelInitializer extends ChannelInitializer<SocketChannel> {
        private final WebHandlers handlers;

        OurChannelInitializer(WebHandlers handlers) {
            this.handlers = handlers;
        }

        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(new HttpContentCompressor((CompressionOptions[]) null));
            p.addLast(new HttpServerExpectContinueHandler());
            p.addLast(new NettyHttpHandler(handlers));
        }
    }
}
