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
package dev.enola.thing.gen;

import java.util.HashSet;
import java.util.Set;

public class Orphanage {
    private final Set<String> orphanCandidates = new HashSet<>();
    private final Set<String> nonOrphans = new HashSet<>();

    public void candidate(String linkIRI) {
        if (!nonOrphans.contains(linkIRI)) orphanCandidates.add(linkIRI);
    }

    public void nonOrphan(String thingIRI) {
        nonOrphans.add(thingIRI);
        // TODO We could optimize and do orphanCandidates.remove(thingIRI); ?
    }

    public Set<String> orphans() {
        // Remove links to all things which were processed after we processed them
        // linkIRIs now contains things which were linked to but that have no properties
        orphanCandidates.removeAll(nonOrphans);
        return orphanCandidates;
    }
}