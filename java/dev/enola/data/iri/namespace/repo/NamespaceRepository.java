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
package dev.enola.data.iri.namespace.repo;

import dev.enola.common.context.TLC;
import dev.enola.data.Repository;

import java.util.Optional;

public interface NamespaceRepository extends Repository<Namespace> {

    static NamespaceRepository ctx() {
        return TLC.optional(NamespaceRepository.class)
                .orElse(NamespaceRepositoryEnolaDefaults.INSTANCE);
    }

    Optional<String> getIRI(String prefix);

    default Optional<Namespace> match(String iri) {
        return stream().filter(namespace -> iri.startsWith(namespace.iri())).findFirst();
    }
}
