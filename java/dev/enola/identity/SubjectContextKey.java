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
package dev.enola.identity;

import dev.enola.common.context.Context;

public enum SubjectContextKey implements Context.Key<Subject> {

    /** The subject of the "currently logged-in user", e.g. from an HTTP request (or similar). */
    USER,

    // TODO ORG_BEHALF ?

    /** The "machine" subject, e.g. the "system" or "application" or "container" or "VM" etc. */
    // TODO ? MACHINE
}
