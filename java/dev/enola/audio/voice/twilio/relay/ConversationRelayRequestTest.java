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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class ConversationRelayRequestTest {

    ConversationRelayIO reader = new ConversationRelayIO();

    @Test
    public void setupMessage() {
        var setup =
                (ConversationRelayRequest.Setup)
                        reader.read(
                                """
                                {"type":"setup","sessionId":"VX3808c8bfc80b383891e28f18d8609ff0","callSid":"CAcd760b1a71b2d557efcf785870e6f09e","parentCallSid":"","from":"+41791234567","to":"+14087153333","forwardedFrom":"+14087153333","callerName":"","direction":"inbound","callType":"PSTN","callStatus":"RINGING","accountSid":"SECRET!","customParameters":{}}
                                """);
        assertThat(setup.sessionId()).isEqualTo("VX3808c8bfc80b383891e28f18d8609ff0");
        assertThat(setup.from()).isEqualTo("+41791234567");
    }

    @Test
    public void promptMessage() {
        var prompt =
                (ConversationRelayRequest.Prompt)
                        reader.read(
                                """
                                {"type":"prompt","voicePrompt":"Hi. Can I make a reservation, please? Hello. I'd like to make a reservation.","lang":"en-US","last":true}
                                """);
        assertThat(prompt.voicePrompt()).startsWith("Hi. Can I");
        assertThat(prompt.lang()).isEqualTo("en-US");
        assertThat(prompt.last()).isTrue();
    }

    @Test
    public void promptMessageWithUnknownExtraField() {
        var prompt =
                (ConversationRelayRequest.Prompt)
                        reader.read(
                                """
                                {"type":"prompt","voicePrompt":"Hi. Can I make a reservation, please? Hello. I'd like to make a reservation.","lang":"en-US","last":true,"extraField":"extraValue"}
                                """);
        assertThat(prompt.voicePrompt()).startsWith("Hi. Can I");
        assertThat(prompt.lang()).isEqualTo("en-US");
        assertThat(prompt.last()).isTrue();
    }

    @Test
    public void dtmfMessage() {
        var dtmf =
                (ConversationRelayRequest.DTMF)
                        reader.read(
                                """
                                {"type":"dtmf","digit":"5"}
                                """);
        assertThat(dtmf.digit()).isEqualTo("5");
    }

    @Test
    public void interruptMessage() {
        var interrupt =
                (ConversationRelayRequest.Interrupt)
                        reader.read(
                                """
                                {"type":"interrupt","utteranceUntilInterrupt":"Hello world","durationUntilInterruptMs":1234}
                                """);
        assertThat(interrupt.utteranceUntilInterrupt()).isEqualTo("Hello world");
        assertThat(interrupt.durationUntilInterruptMs()).isEqualTo(1234);
    }

    @Test
    public void errorMessage() {
        var error =
                (ConversationRelayRequest.Error)
                        reader.read(
                                """
                                {"type":"error","description":"Something went wrong"}
                                """);
        assertThat(error.description()).isEqualTo("Something went wrong");
    }
}
