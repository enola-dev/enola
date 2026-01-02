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
package dev.enola.common.convert;

import java.util.Optional;

/**
 * Optional {@link Converter} of an object of type I to a new object of type O, or {@link
 * Optional#empty()}.
 *
 * @param <I> the type of input objects to convert from
 * @param <O> the type of output objects to convert to, wrapped in an Optional
 */
public interface OptionalConverter<I, O> extends Converter<I, Optional<O>> {}
