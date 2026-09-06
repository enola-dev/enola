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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class MarkdownCanonicalizerTest {

    // TODO Remove all non-double line breaks to avoid discrepancies

    @Test
    void canonicalize() {
        var messy =
                """
                # Heading & One

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

        var clean = new MarkdownCanonicalizer().canonicalize(messy);

        // DiffingStringSubject.assertThat(clean)
        assertThat(clean)
                .isEqualTo(
                        """
                        # Heading & One

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

    @Test
    void canonicalizeAmpersand() {
        var markdown =
                """
                # AT&T & Co.

                Tom & Jerry went to Barnes & Noble.

                * Salt & Pepper
                * R&D and Q&A

                | Key & Val | Info & Details |
                | --- | --- |
                | Fish & Chips | Rock & Roll |

                [Search & Discover](https://example.com?query=a&lang=en&sort=asc)

                `a && b` and `&notAnEntity`

                Literal entity reference: \\&copy;
                Literal numeric entity: \\&#160; and \\&#x26;
                Literal ampersand entity: \\&amp;

                Decoded entities: &copy; and &#160; and &#x26;

                Incomplete non-entities: &copying &foo &123 &# &#x &
                """;

        var clean = new MarkdownCanonicalizer().canonicalize(markdown);

        assertThat(clean)
                .isEqualTo(
                        """
                        # AT&T & Co.

                        Tom & Jerry went to Barnes & Noble.

                        * Salt & Pepper
                        * R&D and Q&A

                        | Key & Val    | Info & Details |
                        | ------------ | -------------- |
                        | Fish & Chips | Rock & Roll    |

                        [Search & Discover](https://example.com?query=a&lang=en&sort=asc)

                        `a && b` and `&notAnEntity`

                        Literal entity reference: \\&copy;
                        Literal numeric entity: \\&#160; and \\&#x26;
                        Literal ampersand entity: \\&amp;

                        Decoded entities: © and   and &

                        Incomplete non-entities: &copying &foo &123 &# &#x &
                        """);
    }

    @Test
    void tableAlignments() {
        var markdown =
                """
                | Left | Center | Right | Default |
                | :--- | :---: | ---: | --- |
                | L1 | C1 | R1 | D1 |
                | Long Left Text | Center | 100 | Default Text |
                """;
        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        | Left           | Center | Right | Default      |
                        | :------------- | :----: | ----: | ------------ |
                        | L1             |   C1   |    R1 | D1           |
                        | Long Left Text | Center |   100 | Default Text |
                        """);
    }

    @Test
    void tableWithInlineFormatting() {
        var markdown =
                """
                | Method | Description | Code |
                | --- | --- | --- |
                | `foo()` | Does **something** cool | `x = y + 1` |
                | [bar](https://example.com) | A *link* example | `None` |
                """;
        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        | Method                     | Description             | Code        |
                        | -------------------------- | ----------------------- | ----------- |
                        | `foo()`                    | Does **something** cool | `x = y + 1` |
                        | [bar](https://example.com) | A *link* example        | `None`      |
                        """);
    }

    @Test
    void tableUnevenRows() {
        var markdown =
                """
                | Col1 | Col2 | Col3 |
                | --- | --- | --- |
                | A | B |
                | X | Y | Z |
                """;
        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        | Col1 | Col2 | Col3 |
                        | ---- | ---- | ---- |
                        | A    | B    |      |
                        | X    | Y    | Z    |
                        """);
    }

    @Test
    void greaterThan() {
        var markdown =
                """
                Three tiers: **Deny > Ask > Allow**

                Inline math: 5 > 3 and x > y

                > This is a real blockquote.

                \\> This is an escaped blockquote at line start.

                * \\> This is an escaped blockquote inside a list item.
                """;
        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        Three tiers: **Deny > Ask > Allow**

                        Inline math: 5 > 3 and x > y

                        > This is a real blockquote.

                        \\> This is an escaped blockquote at line start.

                        * \\> This is an escaped blockquote inside a list item.
                        """);
    }

    @Test
    void frontmatter() {
        var markdown =
                """
                ---
                title: "My Title"
                description: "My Description"
                sources:
                - resource: https://docs.cognee.ai
                  title: "Cognee Documentation"
                - resource: https://github.com/topoteretes/cognee
                  title: "Cognee GitHub Repository"
                ---

                # Heading

                Some text.
                """;
        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean).isEqualTo(markdown);
    }

    @Test
    void magicLinks() {
        var markdown =
                """
                # Overview of [[memory]]

                This references [[memory]] and [[another-doc|Custom Label]].

                Check out [[other#section]] and local anchor [[#heading]].

                External link: [[https://example.com]] and [[https://example.com|Example]].

                * Bullet item with [[memory]]
                - Another item: [[tool]] - A useful tool

                Inside inline code: `[[memory]]`

                ```
                [[memory]]
                ```
                """;

        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        # Overview of [[memory]]

                        This references [[memory]] and [[another-doc|Custom Label]].

                        Check out [[other#section]] and local anchor [[#heading]].

                        External link: [[https://example.com]] and [[https://example.com|Example]].

                        * Bullet item with [[memory]]

                        - Another item: [[tool]] - A useful tool

                        Inside inline code: `[[memory]]`

                        ```
                        [[memory]]
                        ```
                        """);
    }

    @Test
    void magicLinksInTable() {
        var markdown =
                """
                | Topic | Reference |
                | --- | --- |
                | Memory | [[memory]] |
                | Search | [[search\\|Custom Search]] |
                """;

        var clean = new MarkdownCanonicalizer().canonicalize(markdown);
        assertThat(clean)
                .isEqualTo(
                        """
                        | Topic  | Reference                 |
                        | ------ | ------------------------- |
                        | Memory | [[memory]]                |
                        | Search | [[search\\|Custom Search]] |
                        """);
    }
}
