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

import dev.enola.common.jackson.testlib.JsonTester;

import org.junit.Test;

public class ConversationRelayResponseTest {

    @Test
    public void textMessage() throws Exception {
        JsonTester.assertEqualsTo(
                new ConversationRelayResponse.Text("hello, world!", "en", false, true, true),
                """
                {
                  "interruptible": true,
                  "lang": "en",
                  "last": false,
                  "preemptible": true,
                  "token": "hello, world!",
                  "type": "text"
                }
                """);
    }

    @Test
    public void playMessage() throws Exception {
        JsonTester.assertEqualsTo(
                new ConversationRelayResponse.Play(
                        java.net.URI.create("http://example.com/audio.mp3"), true, true),
                """
                {
                  "interruptible": true,
                  "preemptible": true,
                  "source": "http://example.com/audio.mp3",
                  "type": "play"
                }
                """);
    }

    @Test
    public void dtmfMessage() throws Exception {
        JsonTester.assertEqualsTo(
                new ConversationRelayResponse.DTMF("1234"),
                """
                {
                  "digits": "1234",
                  "type": "sendDigits"
                }
                """);
    }

    @Test
    public void languageMessage() throws Exception {
        JsonTester.assertEqualsTo(
                new ConversationRelayResponse.Language("en-US", "en-US"),
                """
                {
                  "transcriptionLanguage": "en-US",
                  "ttsLanguage": "en-US",
                  "type": "language"
                }
                """);
    }

    @Test
    public void endMessage() throws Exception {
        JsonTester.assertEqualsTo(
                new ConversationRelayResponse.End("some-handoff-data"),
                """
                {
                  "handoffData": "some-handoff-data",
                  "type": "end"
                }
                """);
    }
}
