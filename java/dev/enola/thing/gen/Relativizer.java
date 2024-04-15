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
package dev.enola.thing.gen;

import java.net.URI;

public final class Relativizer {

    public static URI relativize(URI thingIRI, String extension) {
        if (thingIRI.getScheme().startsWith("http")) {
            var ssp = thingIRI.getSchemeSpecificPart();
            if (ssp.startsWith("//")) {
                if (ssp.length() > 2) return URI.create(ssp.substring(2) + "." + extension);
                else return URI.create("." + extension);
            } else if (ssp.startsWith("/")) {
                if (ssp.length() > 1)
                    if (ssp.length() > 1) return URI.create(ssp.substring(1) + "." + extension);
                    else return URI.create("." + extension);
            } else return URI.create(ssp + "." + extension);
        }
        throw new IllegalArgumentException(
                "TODO Add missing RelativizerTest coverage for: " + thingIRI);
    }

    private Relativizer() {}
}
