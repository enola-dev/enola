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
package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Strings;

import dev.enola.common.io.object.Identifiable;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

class IdentifiableIdSerializer extends StdSerializer<Identifiable> {

    IdentifiableIdSerializer() {
        super(Identifiable.class);
    }

    @Override
    public void serialize(
            @Nullable Identifiable value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        var id = value.id();
        if (Strings.isNullOrEmpty(id))
            throw JsonMappingException.from(gen, "id() is null or empty: " + value);
        gen.writeString(id);
    }
}
