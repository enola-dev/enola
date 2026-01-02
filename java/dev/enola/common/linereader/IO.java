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
package dev.enola.common.linereader;

import org.jspecify.annotations.Nullable;

public interface IO {

    // TODO readPassword()

    // TODO Markdown?

    // TODO printURL() ?

    // TODO showImage() ??

    // TODO Merge readLine() as readLine(String prompt) with prompt="" ?

    @Nullable String readLine(); // TODO throw IOException ?

    @Nullable String readLine(String prompt);

    default @Nullable String readLine(String promptFormat, Object... args) {
        return readLine(String.format(promptFormat, args));
    }

    void printf(String format, Object... args); // TODO throw IOException ?

    // TODO Rethink this... feel wrong, in hindsight; an IO does not "have" an ExecutionContext?
    //   Rather, one way to implement in IO is to delegate it to an ExecutionContext?
    ExecutionContext ctx();
}
