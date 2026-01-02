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
package dev.enola.data.id;

/**
 * 🆔 annotated classes uniquely 🪪 identify 🫆 objects.
 *
 * <p>{@link IdConverter} converts such IDs from and to String.
 *
 * <p>This is a "convenience marker interface". It is recommended to be added to new classes, but
 * it's technically NOT strictly required; as e.g. an {@link java.net.URI} is also such an ID (and
 * {@link IdConverters#URI} is its converter) or an {@link java.util.UUID} - despite either of them
 * not actually really being annotated with this.
 */
public @interface ID {

    // TODO Class<IdConverter<?>> converter();
}
