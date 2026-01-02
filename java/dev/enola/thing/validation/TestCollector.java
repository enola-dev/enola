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
package dev.enola.thing.validation;

import static java.util.Collections.synchronizedList;

import dev.enola.thing.Thing;

import java.util.ArrayList;
import java.util.List;

/** This implementation is primarily intended to be used by tests. */
public class TestCollector implements Collector2 {

    private final List<Diagnostic> diagnostics = synchronizedList(new ArrayList<>());

    @Override
    public void add(Thing thing, String predicateIRI, String message) {
        diagnostics.add(new Diagnostic(thing.iri(), predicateIRI, message));
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public record Diagnostic(String thingIRI, String predicateIRI, String message) {}
}
