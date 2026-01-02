/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import java.io.Closeable;

/**
 * Closeable Resource.
 *
 * <p>For {@link Resource} implementations which are {@link Closeable} and thus have (!) to be
 * {@link Closeable#close()} after use.
 *
 * <p>Note that the majority of <code>Resource</code> implementations (such as e.g. the {@link
 * FileResource}) themselves are NOT <code>Closeable</code>, because their {@link
 * com.google.common.io.ByteSource} and {@link com.google.common.io.ByteSink} are <a
 * href="https://github.com/google/guava/wiki/IOExplained">designed in (generally) resource leak
 * safe way</a>.
 *
 * <p>There are however some more special case <code>Resource</code> implementations which do
 * require to be closed to avoid resource leaks.
 */
public interface CloseableResource extends Closeable, Resource {}
