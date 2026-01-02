/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.audio.voice.twilio.relay.websocket;

import com.google.common.base.Strings;

import dev.enola.audio.voice.twilio.relay.ConversationHandler;
import dev.enola.audio.voice.twilio.relay.ConversationRelay;
import dev.enola.audio.voice.twilio.security.SignatureValidator;
import dev.enola.common.net.websocket.LoggingWebSocketServer;
import dev.enola.common.secret.SecretManager;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ConversationRelayServer extends LoggingWebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(ConversationRelayServer.class);

    private final ConversationRelay conversationRelay;
    private final SignatureValidator signatureValidator;

    public ConversationRelayServer(
            InetSocketAddress address,
            ConversationRelay conversationRelay,
            SecretManager secretManager)
            throws IOException {
        super(address);
        this.conversationRelay = conversationRelay;
        this.signatureValidator = new SignatureValidator(secretManager);
    }

    public ConversationRelayServer(
            InetSocketAddress address, ConversationHandler handler, SecretManager secretManager)
            throws IOException {
        this(address, new ConversationRelay(handler), secretManager);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        super.onOpen(conn, handshake);

        var remote = conn.getRemoteSocketAddress();
        var host = handshake.getFieldValue("Host");
        var forwardedHosts = handshake.getFieldValue("X-Forwarded-Host");
        var signature = handshake.getFieldValue("x-twilio-signature");
        var requestURI = handshake.getResourceDescriptor();

        if (Strings.isNullOrEmpty(host) && Strings.isNullOrEmpty(forwardedHosts)) {
            var msg = "Cannot validate Twilio Signature; missing Host (and X-Forwarded-Host)";
            logger.error(msg + "; remote={}, requestURI={}", remote, requestURI);
            conn.closeConnection(1002, msg); // 1002 = Protocol Error
            return;
        }

        if (!signatureValidator.validate(host, forwardedHosts, requestURI, signature)) {
            var msg = "Invalid Twilio Signature";
            logger.error(msg + "; remote={}, requestURI={}", remote, requestURI);
            conn.closeConnection(1008, msg); // 1008 = Policy Violation (like HTTP 401 Unauthorized)
            return;
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        super.onMessage(conn, message);

        var jsonResponse = conversationRelay.handle(message);
        if (jsonResponse != null) {
            logger.trace(
                    "WebSocket message response: remote={}, local={}, message={}",
                    conn.getRemoteSocketAddress(),
                    conn.getLocalSocketAddress(),
                    jsonResponse);
            conn.send(jsonResponse);
        }
    }
}
