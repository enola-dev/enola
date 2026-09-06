/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class IndexGeneratorTest {

    @Test
    void generateNewIndexTreeWithDescriptions(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path ai = Files.createDirectories(docs.resolve("ai"));
        Path software = Files.createDirectories(ai.resolve("software"));

        // Article with frontmatter description
        Files.writeString(
                software.resolve("memory.md"),
                """
                ---
                type: Concept
                description: Foundational concepts for LLM agent memory.
                ---

                # Memory Architecture

                Body text that is not used because frontmatter description is present.
                """);

        // Article with inferred description from first paragraph
        Files.writeString(
                software.resolve("agent.md"),
                """
                # Agent OS

                A stateful agent operating system
                with paging and context management.

                ## Features
                - Feature 1
                """);

        // Article without description (heading only)
        Files.writeString(software.resolve("empty.md"), "# Empty Article\n");

        new IndexGenerator().generate(docs);

        // Root index
        Path rootIndex = docs.resolve("index.md");
        assertThat(Files.exists(rootIndex)).isTrue();
        String rootContent = Files.readString(rootIndex);
        assertThat(rootContent)
                .isEqualTo("# Knowledge Wiki\n\n## Subcategories\n\n- [[ai/index]]\n");

        // AI index
        Path aiIndex = ai.resolve("index.md");
        assertThat(Files.exists(aiIndex)).isTrue();
        String aiContent = Files.readString(aiIndex);
        assertThat(aiContent).isEqualTo("# Ai\n\n## Subcategories\n\n- [[software/index]]\n");

        // Software index
        Path softwareIndex = software.resolve("index.md");
        assertThat(Files.exists(softwareIndex)).isTrue();
        String softwareContent = Files.readString(softwareIndex);
        assertThat(softwareContent)
                .isEqualTo(
                        """
                        # Software

                        ## Articles

                        - [[agent]] - A stateful agent operating system with paging and context management.
                        - [[empty]]
                        - [[memory]] - Foundational concepts for LLM agent memory.
                        """);
    }

    @Test
    void updateExistingIndexWithNewSubcategoryAndArticle(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Files.createDirectories(docs.resolve("ai"));
        Files.writeString(docs.resolve("intro.md"), "# Intro\n\nIntro overview.\n");

        String existingIndex =
                """
                ---
                type: Index
                okf_version: 0.2
                ---

                # Custom Docs Title

                Handwritten intro paragraph.

                ## Subcategories

                - [[ai/index]]

                ## Articles

                - [[intro]] - Intro overview.
                """;
        Files.writeString(docs.resolve("index.md"), existingIndex);

        // Add a new subcategory and a new article
        Path systems = Files.createDirectories(docs.resolve("systems"));
        Files.writeString(
                systems.resolve("index.md"),
                "# Systems\n\nHardware and runtime systems documentation.\n");
        Files.writeString(
                docs.resolve("advanced.md"),
                "# Advanced Guide\n\nDeep-dive into system internals.\n");

        new IndexGenerator().generate(docs);

        String updated = Files.readString(docs.resolve("index.md"));
        assertThat(updated).contains("---");
        assertThat(updated).contains("type: Index");
        assertThat(updated).contains("okf_version: 0.2");
        assertThat(updated).contains("# Custom Docs Title");
        assertThat(updated).contains("Handwritten intro paragraph.");
        assertThat(updated)
                .contains(
                        "- [[ai/index]]\n"
                                + "- [[systems/index]] - Hardware and runtime systems"
                                + " documentation.");
        assertThat(updated)
                .contains(
                        "- [[intro]] - Intro overview.\n"
                                + "- [[advanced]] - Deep-dive into system internals.");
    }

    @Test
    void noChangesWhenAlreadyIndexed(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Files.createDirectories(docs.resolve("ai"));
        Files.writeString(docs.resolve("intro.md"), "# Intro\n\nIntro overview.\n");

        String initialIndex =
                """
                # Knowledge Wiki

                ## Subcategories

                - [[ai/index]]

                ## Articles

                - [[intro]] - Intro overview.
                """;
        Files.writeString(docs.resolve("index.md"), initialIndex);

        new IndexGenerator().generate(docs);

        assertThat(Files.readString(docs.resolve("index.md"))).isEqualTo(initialIndex);
    }

    @Test
    void updateExistingIndexWithMissingDescriptions(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Files.writeString(docs.resolve("cmem.md"), "# CMEM\n\nMemory stream for agents.\n");
        Files.writeString(docs.resolve("letta.md"), "# Letta\n\nAgent OS with paging.\n");

        String existingIndexWithoutDescriptions =
                """
                # Memory

                ## Articles

                - [[cmem]]
                - [[letta]]
                """;
        Files.writeString(docs.resolve("index.md"), existingIndexWithoutDescriptions);

        new IndexGenerator().generate(docs);

        String updated = Files.readString(docs.resolve("index.md"));
        assertThat(updated)
                .isEqualTo(
                        """
                        # Memory

                        ## Articles

                        - [[cmem]] - Memory stream for agents.
                        - [[letta]] - Agent OS with paging.
                        """);
    }

    @Test
    void generateNewIndexWithLeadConcept(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("memory.md"),
                """
                ---
                type: Concept
                description: Foundational concepts for LLM agent memory.
                ---

                # LLM and Agent Memory
                """);
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream for agents.\n");
        Files.writeString(memory.resolve("letta.md"), "# Letta\n\nAgent OS with paging.\n");

        new IndexGenerator().generate(docs);

        Path memoryIndex = memory.resolve("index.md");
        assertThat(Files.exists(memoryIndex)).isTrue();
        String content = Files.readString(memoryIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Memory

                        [[memory]] - Foundational concepts for LLM agent memory.

                        ## Articles

                        - [[cmem]] - Memory stream for agents.
                        - [[letta]] - Agent OS with paging.
                        """);
    }

    @Test
    void generateNewIndexWhereOnlyLeadConceptExists(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("memory.md"),
                "# Memory\n\nFoundational concepts for LLM agent memory.\n");

        new IndexGenerator().generate(docs);

        Path memoryIndex = memory.resolve("index.md");
        assertThat(Files.exists(memoryIndex)).isTrue();
        String content = Files.readString(memoryIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Memory

                        [[memory]] - Foundational concepts for LLM agent memory.
                        """);
    }

    @Test
    void updateExistingIndexMigratesLeadConceptFromArticlesToPreamble(@TempDir Path tempDir)
            throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("memory.md"),
                "# Memory\n\nFoundational concepts for LLM agent memory.\n");
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream for agents.\n");
        Files.writeString(memory.resolve("letta.md"), "# Letta\n\nAgent OS with paging.\n");

        String existingIndex =
                """
                # Memory

                ## Articles

                - [[cmem]] - Memory stream for agents.
                - [[memory]] - Old description.
                - [[letta]] - Agent OS with paging.
                """;
        Files.writeString(memory.resolve("index.md"), existingIndex);

        new IndexGenerator().generate(docs);

        String updated = Files.readString(memory.resolve("index.md"));
        assertThat(updated)
                .isEqualTo(
                        """
                        # Memory

                        [[memory]] - Foundational concepts for LLM agent memory.

                        ## Articles

                        - [[cmem]] - Memory stream for agents.
                        - [[letta]] - Agent OS with paging.
                        """);
    }

    @Test
    void updateExistingIndexRefreshesLeadConceptDescription(@TempDir Path tempDir)
            throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("memory.md"), "# Memory\n\nUpdated new description for memory.\n");
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream for agents.\n");

        String existingIndex =
                """
                # Memory

                [[memory]] - Old outdated description.

                ## Articles

                - [[cmem]] - Memory stream for agents.
                """;
        Files.writeString(memory.resolve("index.md"), existingIndex);

        new IndexGenerator().generate(docs);

        String updated = Files.readString(memory.resolve("index.md"));
        assertThat(updated)
                .isEqualTo(
                        """
                        # Memory

                        [[memory]] - Updated new description for memory.

                        ## Articles

                        - [[cmem]] - Memory stream for agents.
                        """);
    }

    @Test
    void generateFromSourceReadmeTransformsToOutputIndex(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path ai = Files.createDirectories(docs.resolve("ai"));
        Files.writeString(ai.resolve("software.md"), "# Software\n\nSoftware overview.\n");

        Files.writeString(
                docs.resolve("README.md"),
                """
                # Enola Knowledge Base

                Knowledge Base (KB) by and for the Enola.dev community.
                """);

        new IndexGenerator().generate(docs);

        Path rootIndex = docs.resolve("index.md");
        assertThat(Files.exists(rootIndex)).isTrue();
        String content = Files.readString(rootIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Enola Knowledge Base

                        Knowledge Base (KB) by and for the Enola.dev community.

                        ## Subcategories

                        - [[ai/index]]
                        """);
    }

    @Test
    void mutualExclusionThrowsWhenBothIndexAndReadmeExist(@TempDir Path tempDir)
            throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Files.writeString(docs.resolve("index.md"), "# Index\n");
        Files.writeString(docs.resolve("README.md"), "# README\n");

        assertThrows(IllegalArgumentException.class, () -> new IndexGenerator().generate(docs));
    }

    @Test
    void mutualExclusionThrowsWhenSubdirectoryHasBothIndexAndReadme(@TempDir Path tempDir)
            throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path sub = Files.createDirectories(docs.resolve("sub"));
        Files.writeString(sub.resolve("index.md"), "# Index\n");
        Files.writeString(sub.resolve("README.md"), "# README\n");

        assertThrows(IllegalArgumentException.class, () -> new IndexGenerator().generate(docs));
    }

    @Test
    void case1SubdirectorySourceReadmeWithoutLeadConcept(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path tools = Files.createDirectories(docs.resolve("tools"));

        Files.writeString(
                tools.resolve("README.md"),
                """
                # Developer Tools

                A curated list of developer productivity tools.
                """);
        Files.writeString(tools.resolve("tool-a.md"), "# Tool A\n\nFirst tool.\n");
        Files.writeString(tools.resolve("tool-b.md"), "# Tool B\n\nSecond tool.\n");

        new IndexGenerator().generate(docs);

        // Subdirectory index.md should be generated from README.md
        Path toolsIndex = tools.resolve("index.md");
        assertThat(Files.exists(toolsIndex)).isTrue();
        assertThat(Files.exists(tools.resolve("README.md")))
                .isTrue(); // Source file remains on disk
        String toolsContent = Files.readString(toolsIndex);
        assertThat(toolsContent)
                .isEqualTo(
                        """
                        # Developer Tools

                        A curated list of developer productivity tools.

                        ## Articles

                        - [[tool-a]] - First tool.
                        - [[tool-b]] - Second tool.
                        """);

        // Parent root index should extract description from tools/README.md
        Path rootIndex = docs.resolve("index.md");
        assertThat(Files.exists(rootIndex)).isTrue();
        String rootContent = Files.readString(rootIndex);
        assertThat(rootContent)
                .isEqualTo(
                        """
                        # Knowledge Wiki

                        ## Subcategories

                        - [[tools/index]] - A curated list of developer productivity tools.
                        """);
    }

    @Test
    void case2DirectoryLeadConceptWithoutReadme(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("memory.md"),
                """
                ---
                type: Concept
                ---

                # LLM and Agent Memory

                Core memory architecture.
                """);
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream.\n");

        new IndexGenerator().generate(docs);

        Path memoryIndex = memory.resolve("index.md");
        assertThat(Files.exists(memoryIndex)).isTrue();
        String content = Files.readString(memoryIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Memory

                        [[memory]] - Core memory architecture.

                        ## Articles

                        - [[cmem]] - Memory stream.
                        """);
    }

    @Test
    void case3DirectoryWithoutLeadConceptOrReadme(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path ai = Files.createDirectories(docs.resolve("ai"));

        Files.writeString(ai.resolve("chat.md"), "# Chat AI\n\nConversational models.\n");
        Files.writeString(ai.resolve("code.md"), "# Code AI\n\nCoding assistants.\n");

        new IndexGenerator().generate(docs);

        Path aiIndex = ai.resolve("index.md");
        assertThat(Files.exists(aiIndex)).isTrue();
        String content = Files.readString(aiIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Ai

                        ## Articles

                        - [[chat]] - Conversational models.
                        - [[code]] - Coding assistants.
                        """);
    }

    @Test
    void case4MergeLeadConceptAndReadme(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("README.md"),
                """
                # Memory Systems

                Custom overview of memory systems.
                """);
        Files.writeString(
                memory.resolve("memory.md"),
                """
                ---
                type: Concept
                ---

                # LLM and Agent Memory

                Core memory architecture.
                """);
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream.\n");
        Files.writeString(memory.resolve("letta.md"), "# Letta\n\nAgent OS.\n");

        new IndexGenerator().generate(docs);

        Path memoryIndex = memory.resolve("index.md");
        assertThat(Files.exists(memoryIndex)).isTrue();
        String content = Files.readString(memoryIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Memory Systems

                        Custom overview of memory systems.

                        [[memory]] - Core memory architecture.

                        ## Articles

                        - [[cmem]] - Memory stream.
                        - [[letta]] - Agent OS.
                        """);
    }

    @Test
    void case4MergeLeadConceptAndIndex(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path memory = Files.createDirectories(docs.resolve("memory"));

        Files.writeString(
                memory.resolve("index.md"),
                """
                # Memory Systems

                Custom overview of memory systems.
                """);
        Files.writeString(
                memory.resolve("memory.md"),
                """
                ---
                type: Concept
                ---

                # LLM and Agent Memory

                Core memory architecture.
                """);
        Files.writeString(memory.resolve("cmem.md"), "# CMEM\n\nMemory stream.\n");

        new IndexGenerator().generate(docs);

        Path memoryIndex = memory.resolve("index.md");
        assertThat(Files.exists(memoryIndex)).isTrue();
        String content = Files.readString(memoryIndex);
        assertThat(content)
                .isEqualTo(
                        """
                        # Memory Systems

                        Custom overview of memory systems.

                        [[memory]] - Core memory architecture.

                        ## Articles

                        - [[cmem]] - Memory stream.
                        """);
    }

    @Test
    void ignoresHiddenDirectoriesAndFiles(@TempDir Path tempDir) throws IOException {
        Path docs = Files.createDirectory(tempDir.resolve("docs"));
        Path hiddenDir = Files.createDirectory(docs.resolve(".git"));
        Files.writeString(hiddenDir.resolve("some.md"), "# Ignored\n");
        Files.writeString(docs.resolve(".hidden.md"), "# Hidden\n");

        new IndexGenerator().generate(docs);

        Path rootIndex = docs.resolve("index.md");
        assertThat(Files.exists(rootIndex)).isTrue();
        assertThat(Files.exists(hiddenDir.resolve("index.md"))).isFalse();
        String content = Files.readString(rootIndex);
        assertThat(content).doesNotContain(".git");
        assertThat(content).doesNotContain("hidden");
    }

    @Test
    void invalidDirectoryThrows(@TempDir Path tempDir) throws IOException {
        Path file = Files.writeString(tempDir.resolve("file.txt"), "hello");
        assertThrows(IllegalArgumentException.class, () -> new IndexGenerator().generate(file));
    }
}
