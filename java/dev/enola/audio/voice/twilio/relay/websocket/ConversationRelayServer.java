/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import dev.enola.audio.voice.twilio.relay.ConversationHandler;
import dev.enola.audio.voice.twilio.relay.ConversationRelay;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ConversationRelayServer extends LoggingWebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(ConversationRelayServer.class);

    private final ConversationRelay conversationRelay;

    public ConversationRelayServer(InetSocketAddress address, ConversationRelay conversationRelay) {
        super(address);
        this.conversationRelay = conversationRelay;
    }

    public ConversationRelayServer(InetSocketAddress address, ConversationHandler handler) {
        this(address, new ConversationRelay(handler));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        super.onMessage(conn, message);

        // TODO Validate X-Twilio-Signature, see
        //   https://www.twilio.com/docs/voice/conversationrelay/websocket-messages

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
