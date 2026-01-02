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

import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.net.MediaType;

import dev.enola.web.WebHandlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.concurrent.TimeUnit;

class NettyHttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpHandler.class);

    private final WebHandlers handlers;

    public NettyHttpHandler(WebHandlers handlerMap) {
        this.handlers = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest req) {

            if (!HttpMethod.GET.equals(req.method())) return;

            var uri = new URI(req.uri());

            ByteBuf content;
            HttpResponseStatus status;
            MediaType mediaType;
            try {
                var futureResource = handlers.handle(uri);
                // TODO Futures.addCallback() for futureResource.addListener(); to Async this!
                // TODO does Netty timeout requests after a while as well?
                // TODO Make this "streaming", for "big" Resources!

                var resource = futureResource.get(17, TimeUnit.SECONDS);
                // TODO Charset convert this, if required!
                content = Unpooled.wrappedBuffer(resource.byteSource().read());
                mediaType = resource.mediaType();
                status = OK;

            } catch (Throwable e) {
                LOG.error("Failed to handle {}", uri, e);

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                content = Unpooled.wrappedBuffer(sw.getBuffer().toString().getBytes(UTF_8));

                mediaType = PLAIN_TEXT_UTF_8;
                status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            }

            FullHttpResponse response =
                    new DefaultFullHttpResponse(req.protocolVersion(), status, content);
            response.headers()
                    .set(CONTENT_TYPE, mediaType.toString())
                    .setInt(CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(req);
            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                }
            } else {
                response.headers().set(CONNECTION, CLOSE);
            }

            ChannelFuture f = ctx.write(response);

            if (!keepAlive) {
                ignore(f.addListener(ChannelFutureListener.CLOSE));
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.warn("HTTP handling error", cause);
        // TODO Shouldn't this also close, as above?
        ignore(ctx.close());
    }

    private void ignore(ChannelFuture future) {}
}
