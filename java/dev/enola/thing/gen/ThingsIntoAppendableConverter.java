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
package dev.enola.thing.gen;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterIntoAppendable;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.thing.Thing;

import java.io.IOException;

public interface ThingsIntoAppendableConverter extends ConverterIntoAppendable<Iterable<Thing>> {

    default void convertIntoOrThrow(Iterable<Thing> things, WritableResource into)
            throws ConversionException, IOException {
        try (var out = into.charSink().openBufferedStream()) {
            convertIntoOrThrow(things, out);
        }
    }

    default String label(Metadata metadata) {
        return metadata.emoji() + metadata.label();
    }
}
