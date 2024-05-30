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
package dev.enola.thing;

import dev.enola.common.convert.*;

// TODO Make this extensible? E.g. using ServiceLoader.
class ThingObjectClassConverter extends ObjectClassConverterChain {

    static ObjectClassConverter INSTANCE = new ThingObjectClassConverter();

    private ThingObjectClassConverter() {
        super(
                new IdentityObjectClassConverter(),
                new ObjectConverter<>(Link.class, String.class, link -> link.iri()),
                new ObjectConverter<>(LangString.class, String.class, in -> in.text()),
                // TODO Following is all wrong, and only works by chance, because it's last...
                // ObjectToStringBiConverters must be fixed to consider the type, before toString!
                // If PredicatesObjects getOptional() would use the Datatype, we wouldn't need this!
                ObjectToStringBiConverters.INSTANT);
        // TODO Auto-discover ObjectToStringBiConverters.BOOLEAN, .INT, .URI, .LOCAL_DATE ?
    }
}
