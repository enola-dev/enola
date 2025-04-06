/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.identity;

import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.thing.HasIRI;
import dev.enola.thing.java.HasType;

/**
 * Subject. Often a user (person) or an organization / group, but also a machine, or a service, etc.
 *
 * <p>It may be a "tangible" real-world entity, or an "intangible" virtual entity.
 *
 * <p>Could be "Alice", or "Bob" - or... YOU!
 */
// TODO HasPublicKeys ?
public interface Subject extends HasIRI, HasType, HasLabel {

    // skipcq: JAVA-E0169
    interface Builder extends HasType.Builder<Subject>, HasLabel.Builder<Subject> {
        /* TODO HasIRI.Builder<Builder>, */

        @Override
        Builder iri(String iri);

        Subject build();
    }
}
