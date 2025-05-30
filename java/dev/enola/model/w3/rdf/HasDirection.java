/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.model.w3.rdf;

import dev.enola.common.text.Direction;
import dev.enola.thing.KIRI;
import dev.enola.thing.impl.IImmutableThing;

public interface HasDirection extends IImmutableThing {

    default Direction locale() {
        var opt = getOptional(KIRI.RDF.DIRECTION, String.class);
        return opt.map(Direction::from).orElse(Direction.AUTO);
    }
}
