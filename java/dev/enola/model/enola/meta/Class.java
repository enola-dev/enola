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
package dev.enola.model.enola.meta;

import java.util.Set;

// skipcq: JAVA-E0169
public interface Class extends Type, dev.enola.model.w3.rdfs.Class {

    // TODO @IRI(KIRI.E.META.PARENTS) ?
    Set<Class> parents();

    // TODO @IRI(KIRI.E.META.PROPERTIES)
    // Cannot be properties() due to conflict
    Set<Property> classProperties();

    String iriTemplate();

    interface Builder<B extends Class> extends Type.Builder<B> { // skipcq: JAVA-E0169

        Builder<B> addParent(Class parent);

        Builder<B> addClassProperty(Property property);

        Builder<B> iriTemplate(String iriTemplate);
    }
}
