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
package dev.enola.thing;

import dev.enola.datatype.Datatype;

/**
 * Literal is a value with datatype (IRI) which can appear in the {@link Thing#properties()} Map
 * values.
 *
 * <p>Normally, values are some other native Java object; this is only for cases which could not be
 * decoded to a more suitable Java type, because no {@link Datatype} for this literal's datatype IRI
 * was registered.
 */
public final record Literal(String value, String datatypeIRI) {}
