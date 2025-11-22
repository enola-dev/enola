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

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

class ConversationRelayServer extends LoggingWebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(ConversationRelayServer.class);

    public ConversationRelayServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // TODO Change info to debug - and actually parse the JSON, into ConversationRelay
        logger.info("WebSocket message received: message={}", message);
    }
}
