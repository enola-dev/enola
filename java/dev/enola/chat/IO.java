/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.chat;

import org.jspecify.annotations.Nullable;

public interface IO {

    // TODO Markdown?

    // TODO printURL() ?

    // TODO showImage() ??

    @Nullable String readLine();

    @Nullable String readLine(String prompt);

    default @Nullable String readLine(String promptFormat, Object... args) {
        return readLine(String.format(promptFormat, args));
    }

    void printf(String format, Object... args);
}
