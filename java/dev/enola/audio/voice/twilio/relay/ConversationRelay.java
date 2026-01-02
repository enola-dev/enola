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
package dev.enola.audio.voice.twilio.relay;

import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.DTMF;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Error;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Interrupt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Prompt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Setup;

import org.jspecify.annotations.Nullable;

public final class ConversationRelay {

    private final ConversationRelayIO io;
    private final ConversationHandler handler;

    public ConversationRelay(ConversationHandler handler, ConversationRelayIO io) {
        this.handler = handler;
        this.io = io;
    }

    public ConversationRelay(ConversationHandler handler) {
        this(handler, new ConversationRelayIO());
    }

    public final @Nullable String handle(String json) {
        var request = io.read(json);
        var response =
                switch (request) {
                    case Setup setup -> handler.onSetup(setup);
                    case Prompt prompt -> handler.onPrompt(prompt);
                    case DTMF dtmf -> handler.onDTMF(dtmf);
                    case Interrupt interrupt -> handler.onInterrupt(interrupt);
                    case Error error -> handler.onError(error);
                };
        if (response != null) {
            return io.write(response);
        } else {
            return null;
        }
    }
}
