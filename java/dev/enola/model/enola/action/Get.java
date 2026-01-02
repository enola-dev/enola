/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.model.enola.action;

import com.google.common.reflect.TypeToken;

import dev.enola.Action;
import dev.enola.data.ProviderFromIRI;

// Get.IRI
public class Get implements Action<String, Object> {
    public static final String IRI = "https://enola.dev/get";

    private final ProviderFromIRI<?> provider;

    public Get(ProviderFromIRI<?> provider) {
        this.provider = provider;
    }

    @Override
    public String iri() {
        return IRI;
    }

    @Override
    public TypeToken<String> argumentType() {
        return new TypeToken<String>() {};
    }

    @Override
    public TypeToken<Object> returnType() {
        return new TypeToken<Object>() {};
    }

    @Override
    public Object act(String iri) {
        return provider.get(iri);
    }
}
