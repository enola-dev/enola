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
package dev.enola.model.w3.rdfs;

import dev.enola.model.w3.rdf.HasClass;
import dev.enola.thing.Thing;

// TODO Is this really needed? Does RDFS really define this?!? Don't make shit up... ;-)
public interface Resource extends Thing, HasClass, HasLabel {

    // skipcq: JAVA-E0169
    interface Builder<B extends Resource> extends HasClass.Builder<B>, HasLabel.Builder<B> {}
}
