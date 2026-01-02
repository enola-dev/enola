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
package dev.enola.common.markdown;

import com.google.common.truth.Truth;

import org.junit.Test;

public class MarkdownTest {

    // TODO Remove all non-double line breaks to avoid discrepancies

    @Test
    public void canonicalize() {
        var messy =
                """
                # Heading One

                This is some text with  \t trailing spaces.


                * ItemOne
                - Item Two
                  * Sub-item
                __This is bold__ and _this is italic_.

                1. one
                1. two

                ```java
                System.out.println("Hello");
                ```


                A paragraph with some text.



                Another paragraph.\
                """;

        var clean = Markdown.canonicalize(messy);

        // DiffingStringSubject.assertThat(clean)
        Truth.assertThat(clean)
                .isEqualTo(
                        """
                        # Heading One

                        This is some text with  \t trailing spaces.

                        * ItemOne

                        - Item Two
                          * Sub-item
                            **This is bold** and _this is italic_.

                        1. one
                        2. two

                        ```java
                        System.out.println("Hello");
                        ```

                        A paragraph with some text.

                        Another paragraph.
                        """);
    }
}
