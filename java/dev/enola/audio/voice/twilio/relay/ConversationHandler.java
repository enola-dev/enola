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
package dev.enola.audio.voice.twilio.relay;

import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.DTMF;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Error;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Interrupt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Prompt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Setup;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConversationHandler {

    static final Logger logger = LoggerFactory.getLogger(ConversationHandler.class);

    default @Nullable ConversationRelayResponse onSetup(Setup setup) {
        return null;
    }

    default @Nullable ConversationRelayResponse onPrompt(Prompt prompt) {
        return null;
    }

    default @Nullable ConversationRelayResponse onDTMF(DTMF dtmf) {
        return null;
    }

    default @Nullable ConversationRelayResponse onInterrupt(Interrupt interrupt) {
        return null;
    }

    default @Nullable ConversationRelayResponse onError(Error error) {
        logger.error("Error: {}", error.description());
        return null;
    }
}
