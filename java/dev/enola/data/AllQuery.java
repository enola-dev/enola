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
package dev.enola.data;

// For "enola:/" & "enola:/inline" (later to be changed)
// (from ListThingService.ENOLA_ROOT_LIST_IRIS & ENOLA_ROOT_LIST_THINGS)
public record AllQuery(boolean inline, long limit) implements Query {
    // TODO Implement from/toString, using dev.enola.data.id
}
