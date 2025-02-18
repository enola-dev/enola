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

import dev.enola.model.w3.rdfs.Class;
import dev.enola.model.w3.rdfs.Resource;

import java.util.Optional;

public interface Property extends Resource {

    default Optional<Property> subPropertyOf() {
        return getThing("http://www.w3.org/2000/01/rdf-schema#subPropertyOf", Property.class);
    }

    default Optional<Class> domain() {
        return getThing("http://www.w3.org/2000/01/rdf-schema#domain", Class.class);
    }

    default Optional<Class> range() {
        return getThing("http://www.w3.org/2000/01/rdf-schema#range", Class.class);
    }
}
