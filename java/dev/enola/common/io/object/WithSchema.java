/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class WithSchema {
    // TODO Move to new dev.enola.common.io.object.schema package later
    //   Actually, create a separate .model sub-package for types, separate from code
    // TODO Introduce interface HasSchema { URI schema(); } class WithSchema implements HasSchema
    // TODO Enable Validation against e.g. JSON (and later other) Schema/s

    @JsonProperty("$schema")
    public URI schema;
}
