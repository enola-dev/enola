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

import com.google.common.base.Strings;

import java.net.URI;
import java.util.Map;

final class Providers {

    static String model(URI uri, Map<String, String> queryMap, Provider<?> provider) {
        var model = queryMap.get("model");
        if (!Strings.isNullOrEmpty(model)) return model;

        throw new IllegalArgumentException(
                uri + "; use e.g. " + provider.uriExamples() + ", see " + provider.docURL());
    }

    private Providers() {}
}
