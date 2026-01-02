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

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;

public final class Colorizer {

    public static String hexColor(Thing thing) {
        String color;
        var colorProperty = thing.get(KIRI.E.COLOR, String.class);
        if (colorProperty != null) return colorProperty;
        return getColorCodeFromHash(thing.iri());
    }

    private static String getColorCodeFromHash(String inputString) {
        int hashCode = inputString.hashCode();
        hashCode = Math.abs(hashCode);
        int red = (hashCode & 0xFF0000) >> 16;
        int green = (hashCode & 0x00FF00) >> 8;
        int blue = hashCode & 0x0000FF;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    private Colorizer() {}
}
