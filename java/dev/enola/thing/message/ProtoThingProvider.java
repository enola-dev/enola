/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.message;

import com.google.protobuf.Any;

import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.ThingProvider;

import org.jspecify.annotations.Nullable;

/** Proto Thing variant of {@link ThingProvider} (which is for Java Things). */
public interface ProtoThingProvider extends ProviderFromIRI<Any> {
    // TODO This should eventually replace all uses of ThingService...

    // TODO Replace Any with Things, by moving EnolaThingProvider's logic into core
    @Override
    @Nullable Any get(String iri); // TODO throws ?? UncheckedIOException, ConversionException;
}
