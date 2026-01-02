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

/**
 * hashbrown contains utilities related to <a
 * href="https://en.wikipedia.org/wiki/Cryptographic_hash_function">hashing</a> which are useful for
 * <a href="https://en.wikipedia.org/wiki/File_verification">integrity verifications</a> and <a
 * href="https://en.wikipedia.org/wiki/Content-addressable_storage">Content-addressable storage</a>
 * (CAS).
 *
 * <p>It's named after <a href="https://en.wikipedia.org/wiki/Hash_browns">Hash browns</a> (AKA
 * "Rösti"), which the original author of this package (Michael Vorburger.ch) loves.
 */
@NullMarked
package dev.enola.common.io.hashbrown;

import org.jspecify.annotations.NullMarked;
