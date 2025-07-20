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
package dev.enola.ai.adk.test;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.BaseLlmConnection;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.LlmResponse;

import io.reactivex.rxjava3.core.Flowable;

public class EchoModel extends BaseLlm {

    public EchoModel() {
        super("Echo");
    }

    @Override
    public Flowable<LlmResponse> generateContent(LlmRequest llmRequest, boolean stream) {
        var requestContents = llmRequest.contents();
        if (requestContents.size() == 1) {
            var requestContent = requestContents.getFirst();
            return Flowable.just(LlmResponse.builder().content(requestContent).build());
        } else if (requestContents.isEmpty()) {
            return Flowable.just(LlmResponse.builder().build());
        } else // requestContents.size() > 1
            // TODO Or should we just log a warning and return only the first content?!
            throw new IllegalArgumentException("Cannot echo requests with more than one content");
    }

    @Override
    public BaseLlmConnection connect(LlmRequest llmRequest) {
        // TODO How should EchoModel implement connect() ?! See also MockModel.
        throw new UnsupportedOperationException("EchoModel does not yet support connections!");
    }
}
