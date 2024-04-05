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
package dev.enola.datatype;

import dev.enola.data.Repository;

import java.util.Optional;

public interface DatatypeRepository extends Repository<Datatype<?>> {

    Optional<Datatype<?>> match(String text);

    // TODO <T> Optional<Datatype<T>> get(Class<T> type);
    // Wait, this is tricky... ;-) several datatypes, such as STRING and IRI, can *both* be of
    // javaType String.class... so there needs to be some way to ask each one if they're it?
}
