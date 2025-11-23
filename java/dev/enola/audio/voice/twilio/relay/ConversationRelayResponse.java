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

import dev.enola.audio.voice.twilio.relay.ConversationRelayResponse.DTMF;
import dev.enola.audio.voice.twilio.relay.ConversationRelayResponse.End;
import dev.enola.audio.voice.twilio.relay.ConversationRelayResponse.Language;
import dev.enola.audio.voice.twilio.relay.ConversationRelayResponse.Play;
import dev.enola.audio.voice.twilio.relay.ConversationRelayResponse.Text;

import java.net.URI;

/**
 * Twilio's <a
 * href="https://www.twilio.com/docs/voice/conversationrelay/websocket-messages">ConversationRelay
 * response messages</a> JSON format.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Text.class, name = "text"),
    @JsonSubTypes.Type(value = Play.class, name = "play"),
    @JsonSubTypes.Type(value = DTMF.class, name = "sendDigits"),
    @JsonSubTypes.Type(value = Language.class, name = "language"),
    @JsonSubTypes.Type(value = End.class, name = "end")
})
public sealed interface ConversationRelayResponse {

    record Text(String token, String lang, boolean last, boolean interruptible, boolean preemptible)
            implements ConversationRelayResponse {}

    record Play(URI source, boolean interruptible, boolean preemptible)
            implements ConversationRelayResponse {}

    record DTMF(String digits) implements ConversationRelayResponse {}

    // TODO Use Locale for ttsLanguage and transcriptionLanguage
    record Language(String ttsLanguage, String transcriptionLanguage)
            implements ConversationRelayResponse {}

    // TODO Should handoffData be Map<String, Object> instead of String?
    record End(String handoffData) implements ConversationRelayResponse {}
}
