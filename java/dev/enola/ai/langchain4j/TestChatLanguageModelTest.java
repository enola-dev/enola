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
package dev.enola.ai.langchain4j;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class TestChatLanguageModelTest {

    @Test
    public void mock() throws TimeoutException {
        var model = new TestChatLanguageModel("Zurich");
        var answer = new TestStreamingChatResponseHandler();
        model.chat("List top 3 cites in Switzerland", answer);
        assertThat(answer.awaitChatResponse(Duration.ofSeconds(1)).aiMessage().text())
                .contains("Zurich");
    }
}
