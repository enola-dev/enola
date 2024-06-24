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
package dev.enola.model.enola.java;

import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.model.w3.rdfs.Typed;
import dev.enola.thing.java.IRI;

/** 📦 <a href="https://docs.enola.dev/models/enola.dev/java/package/">Java Package</a>. */
@IRI("https://enola.dev/java/Package/{FQN}")
public interface Package
        extends Typed, HasLabel { // NOT dev.enola.model.w3.rdfs.Class; these are the instances
}
