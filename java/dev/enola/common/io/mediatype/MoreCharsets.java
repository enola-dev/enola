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
package dev.enola.common.io.mediatype;

import java.nio.charset.Charset;

/**
 * See JDK's {@link java.nio.charset.StandardCharsets} and Guava's {@link
 * java.nio.charset.StandardCharsets}.
 */
public final class MoreCharsets {

    // TODO with & without BOM byte order mark... see
    // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html

    public static final Charset UTF_32BE = Charset.forName("UTF-32BE");

    public static final Charset UTF_32LE = Charset.forName("UTF_32LE");

    private MoreCharsets() {}
}
