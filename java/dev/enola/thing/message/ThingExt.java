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
package dev.enola.thing.message;

import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.ThingOrBuilder;
import dev.enola.thing.proto.Value;

/** Extension methods for {@link Thing}. */
public final class ThingExt {
    // TODO Rename proto.Things to ThingList and mv this from ThingExt to Things

    public static void setString(Thing.Builder thing, String propertyIRI, String string) {
        var value = Value.newBuilder().setString(string).build();
        thing.putProperties(propertyIRI, value);
    }

    public static String getString(ThingOrBuilder thing, String propertyIRI) {
        var value = thing.getPropertiesMap().get(propertyIRI);
        if (value == null) return null;
        return value.getString();
    }

    private ThingExt() {}
}
