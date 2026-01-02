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
package dev.enola.common.convert;

import dev.enola.common.ByteSeq;
import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;

import io.ipfs.multibase.Multibase;

import org.jspecify.annotations.Nullable;

/**
 * <a href="https://github.com/multiformats/multibase">Multibase</a> &lt;=&gt; {@link ByteSeq}
 * Converter.
 */
final class MultibaseConverter implements ObjectToStringBiConverter<ByteSeq> {

    // Currently implemented with https://github.com/multiformats/java-multibase;
    // https://github.com/filip26/copper-multibase could be an alternative (if issues).

    public enum Base implements Context.Key<Multibase.Base> {
        DEFAULT
    }

    @Override
    public @Nullable ByteSeq convertFrom(@Nullable String input) throws ConversionException {
        if (input == null) return null;
        try {
            return ByteSeq.from(Multibase.decode(input));
        } catch (Exception e) {
            throw new ConversionException(input, e);
        }
    }

    @Override
    public @Nullable String convertTo(@Nullable ByteSeq input) throws ConversionException {
        if (input == null) return null;
        var base = TLC.optional(Base.DEFAULT).orElse(Multibase.Base.Base64Url);
        return Multibase.encode(base, input.toBytes());
    }
}
