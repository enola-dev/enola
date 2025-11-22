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

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.enola.common.jackson.ObjectMappers;

import java.io.IOException;

public class ConversationRelayIO {

    private static final ObjectMapper objectMapper =
            ObjectMappers.newObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ConversationRelayRequest read(String json) {
        try {
            return requireNonNull(objectMapper.readValue(json, ConversationRelayRequest.class));
        } catch (IOException e) {
            // Intentionally not including full json in exception, as it may contain sensitive data
            // If it's required for debugging in the future, then log it at DEBUG level
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }

    public String write(ConversationRelayResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (IOException e) {
            // Intentionally not including full response, as it may contain sensitive data
            // If it's required for debugging in the future, then log it at DEBUG level
            throw new IllegalArgumentException(
                    "JSON marshalling failed for: " + response.getClass().getName(), e);
        }
    }
}
