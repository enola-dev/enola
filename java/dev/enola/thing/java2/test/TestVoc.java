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
package dev.enola.thing.java2.test;

import dev.enola.thing.java2.Vocabulary;

/// Constants for the `https://example.org` vocabulary.
///
/// You typically use the strongly typed interfaces in this package instead of this directly.
@Vocabulary
public final class TestVoc {

    // This intentionally does not declare any namespace prefix; that does not belong here.
    public static final String NS = "https://example.org/";

    public static final class A {
        public static final String A = NS + "a";

        private A() {}
    }

    public static final class B {
        public static final String B = NS + "b";

        private B() {}
    }

    public static final class SOMETHING {
        public static final String TEST = NS + "test";

        private SOMETHING() {}
    }

    private TestVoc() {}
}
