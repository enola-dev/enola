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
package dev.enola.thing.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;

import java.io.IOException;

/**
 * Converts a {@link Thing} to a {@link Appendable} using internal technical toString().
 *
 * <p>Only intended for debugging and testing, and as a fallback when no other implementation is
 * available. Prefer using e.g. the JavaThingIntoRdfAppendableConverter for real world production
 * use.
 */
public class ToStringThingIntoAppendableConverter implements ThingIntoAppendableConverter {

    @Override
    public boolean convertInto(Thing from, Appendable into)
            throws ConversionException, IOException {

        into.append(from.toString());
        return true;
    }
}
