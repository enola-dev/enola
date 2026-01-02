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
package dev.enola.ai.adk.test;

import com.google.adk.models.LlmRequest;
import com.google.adk.models.LlmResponse;
import com.google.genai.types.Content;

import io.reactivex.rxjava3.core.Flowable;

public class EchoModel extends TestLlm {

    public EchoModel() {
        // TODO Fix this ugly temporary hack...
        super(() -> null);
        this.responsesSupplier = () -> Flowable.just(echo(getLastRequest()));
    }

    private LlmResponse echo(LlmRequest llmRequest) {
        var builder = LlmResponse.builder();
        llmRequest.contents().stream()
                .map(
                        content -> {
                            var copy = Content.builder().role("model");
                            content.parts().ifPresent(copy::parts);
                            return copy.build();
                        })
                .forEach(builder::content);
        return builder.build();
    }
}
