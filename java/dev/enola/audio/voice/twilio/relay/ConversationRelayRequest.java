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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.DTMF;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Error;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Interrupt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Prompt;
import dev.enola.audio.voice.twilio.relay.ConversationRelayRequest.Setup;

import java.util.Map;

/**
 * Twilio's <a
 * href="https://www.twilio.com/docs/voice/conversationrelay/websocket-messages">ConversationRelay
 * request messages</a> JSON format.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Setup.class, name = "setup"),
    @JsonSubTypes.Type(value = Prompt.class, name = "prompt"),
    @JsonSubTypes.Type(value = DTMF.class, name = "dtmf"),
    @JsonSubTypes.Type(value = Interrupt.class, name = "interrupt"),
    @JsonSubTypes.Type(value = Error.class, name = "error")
})
public sealed interface ConversationRelayRequest {

    record Setup(
            String sessionId,
            String callSid,
            String parentCallSid,
            String from,
            String to,
            String forwardedFrom,
            String callerName,
            // TODO Use direction enum; escalate to Twilio for missing Source of Truth documentation
            String direction,
            // TODO Use callType enum; escalate to Twilio for missing Source of Truth documentation
            String callType,
            // TODO Use https://www.twilio.com/docs/voice/api/call-resource#call-status-values enum
            String callStatus,
            String accountSid,
            Map<String, Object> customParameters)
            implements ConversationRelayRequest {}

    // TODO Use Locale for lang
    record Prompt(String voicePrompt, String lang, boolean last)
            implements ConversationRelayRequest {}

    record DTMF(String digit) implements ConversationRelayRequest {}

    record Interrupt(String utteranceUntilInterrupt, int durationUntilInterruptMs)
            implements ConversationRelayRequest {}

    record Error(String description) implements ConversationRelayRequest {}
}
