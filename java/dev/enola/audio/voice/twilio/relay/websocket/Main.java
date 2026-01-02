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

import dev.enola.audio.voice.twilio.relay.EchoConversationHandler;
import dev.enola.common.ShutdownCloser;
import dev.enola.common.logging.JavaUtilLogging;
import dev.enola.common.secret.auto.AutoSecretManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8888;
        var address = new InetSocketAddress(host, port);

        JavaUtilLogging.configure(Level.ALL);

        // TODO Add a (localhost only) /quitquit sort of command to close() the server

        // TODO Move Main class to another module, and replace echo with AI conversation handler...
        var handler = new EchoConversationHandler();

        ShutdownCloser.add(
                new ConversationRelayServer(address, handler, AutoSecretManager.INSTANCE()));
    }
}
