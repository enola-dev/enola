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
package dev.enola.ai.iri;

import static dev.enola.common.MoreMaps.ifPresent;

import static java.lang.Double.parseDouble;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.io.iri.URIs;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ModelConfig {

    // TODO Also `thinking`, `maxSteps`, `seed`, `stopSequences` ?

    private Optional<Double> temperature = Optional.empty();
    private Optional<Double> topP = Optional.empty();
    private Optional<Double> topK = Optional.empty();
    private Optional<Integer> maxOutputTokens = Optional.empty();

    public static ModelConfig from(Map<String, String> parameters) {
        var config = new ModelConfig();
        ifPresent(parameters, "temperature", temp -> config.temperature(parseDouble(temp)));
        ifPresent(parameters, "topP", topP -> config.topP(parseDouble(topP)));
        ifPresent(parameters, "topK", topK -> config.topK(parseDouble(topK)));
        ifPresent(
                parameters,
                "maxOutputTokens",
                maxOutputTokens -> config.maxOutputTokens(Integer.parseInt(maxOutputTokens)));
        return config;
    }

    public static ModelConfig from(URI uri) {
        return from(URIs.getQueryMap(uri));
    }

    public static URI temperature(URI uri, double temperature) {
        return URIs.addQuery(uri, Map.of("temperature", Double.toString(temperature)));
    }

    public Optional<Double> temperature() {
        return temperature;
    }

    @CanIgnoreReturnValue
    public ModelConfig temperature(double temperature) {
        this.temperature = Optional.of(temperature);
        return this;
    }

    public Optional<Double> topP() {
        return topP;
    }

    @CanIgnoreReturnValue
    public ModelConfig topP(double topP) {
        this.topP = Optional.of(topP);
        return this;
    }

    public Optional<Double> topK() {
        return topK;
    }

    @CanIgnoreReturnValue
    public ModelConfig topK(double topK) {
        this.topK = Optional.of(topK);
        return this;
    }

    public Optional<Integer> maxOutputTokens() {
        return maxOutputTokens;
    }

    @CanIgnoreReturnValue
    public ModelConfig maxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = Optional.of(maxOutputTokens);
        return this;
    }

    public boolean isEmpty() {
        return temperature.isEmpty()
                && topP.isEmpty()
                && topK.isEmpty()
                && maxOutputTokens.isEmpty();
    }
}
