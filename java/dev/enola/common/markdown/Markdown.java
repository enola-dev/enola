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

import static dev.enola.common.io.file.FilePaths.copyDirectory;
import static dev.enola.common.io.file.FilePaths.isIgnored;
import static dev.enola.common.io.file.FilePaths.requireDirectory;

import dev.enola.common.io.file.FilePaths;
import dev.enola.common.io.http.client.HttpGetter;
import dev.enola.common.io.http.client.jdk.HttpClient;
import dev.enola.common.io.mediatype.MediaTypeDetector;
import dev.enola.common.io.mediatype.MediaTypes;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.parser.RawContentParser;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.parser.IncludeSourceSpans;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class Markdown {

    static final List<Extension> EXTENSIONS =
            List.of(
                    YamlFrontMatterExtension.create(new RawContentParser.Factory()),
                    TablesExtension.create(),
                    HeadingAnchorExtension.create(),
                    AutolinkExtension.create(),
                    TaskListItemsExtension.create());

    static final List<Extension> RENDERER_EXTENSIONS =
            EXTENSIONS.stream().filter(e -> !(e instanceof TablesExtension)).toList();

    public static final Parser PARSER =
            Parser.builder()
                    .extensions(EXTENSIONS)
                    .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
                    .linkProcessor(new MagicLinkProcessor())
                    .build();

    private static final HttpGetter HTTP = new HttpClient();
    private static final MagicLinkResolver MAGIC_LINK_RESOLVER = new MagicLinkResolver(HTTP);

    private static final MarkdownCanonicalizer CANONICALIZER = new MarkdownCanonicalizer();
    private static final IndexGenerator INDEX_GENERATOR = new IndexGenerator();
    private static final HtmlGenerator HTML = new HtmlGenerator();

    private Markdown() {}

    /** Canonicalizes Markdown text. */
    public static String canonicalize(String markdown) {
        return CANONICALIZER.canonicalize(markdown);
    }

    /**
     * Canonicalizes all Markdown files in {@code path} (and its subdirectories) in parallel,
     * modifying them in place. If {@code path} is a regular file, canonicalizes that file in place.
     *
     * @param path the directory or file to canonicalize
     */
    public static void canonicalize(Path path) {
        if (Files.isRegularFile(path)) {
            try {
                String content = Files.readString(path);
                String canonicalized = CANONICALIZER.canonicalize(content);
                if (!content.equals(canonicalized)) {
                    Files.writeString(path, canonicalized);
                }
                return;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        FilePaths.requireDirectory(path);
        try {
            List<Path> files;
            try (Stream<Path> stream = Files.walk(path)) {
                files =
                        stream.filter(Files::isRegularFile)
                                .filter(p -> !isIgnored(path, p))
                                .filter(Markdown::isMarkdown)
                                .toList();
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>(files.size());
            for (Path file : files) {
                futures.add(
                        CompletableFuture.runAsync(
                                () -> {
                                    try {
                                        String content = Files.readString(file);
                                        String canonicalized = CANONICALIZER.canonicalize(content);
                                        if (!content.equals(canonicalized)) {
                                            Files.writeString(file, canonicalized);
                                        }
                                    } catch (IOException e) {
                                        throw new UncheckedIOException(e);
                                    }
                                }));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Ingests Markdown sources from {@code inputDirectory}, synthesizes directory {@code index.md}
     * files in memory, resolves magical short [[related]] links, and writes resolved Markdown files
     * and copied asset files to {@code outputDirectory}.
     *
     * @param inputDirectory the root directory containing source Markdown and asset files
     * @param outputDirectory the directory to write resolved Markdown and asset files to
     */
    public static void md2md(Path inputDirectory, Path outputDirectory) {
        try {
            Files.createDirectories(outputDirectory);
            Map<Path, String> inputMarkdown = readMarkdownFiles(requireDirectory(inputDirectory));
            Map<Path, String> withIndexes = INDEX_GENERATOR.generate(inputDirectory, inputMarkdown);
            Map<Path, String> resolved = MAGIC_LINK_RESOLVER.resolve(withIndexes);

            for (var entry : resolved.entrySet()) {
                Path destPath = outputDirectory.resolve(entry.getKey());
                if (destPath.getParent() != null) {
                    Files.createDirectories(destPath.getParent());
                }
                Files.writeString(destPath, entry.getValue());
            }

            copyDirectory(
                    inputDirectory,
                    outputDirectory,
                    p -> !isIgnored(inputDirectory, p) && !isMarkdown(p));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Ingests Markdown sources from {@code inputDirectory}, synthesizes directory {@code index.md}
     * files in memory, resolves magical short [[related]] links in memory, and writes converted
     * {@code *.html} files, resolved {@code *.md} files, {@code wiki.css}, {@code wiki.js}, and
     * copied asset files to {@code outputDirectory}.
     *
     * @param inputDirectory the root directory containing source Markdown and asset files
     * @param outputDirectory the directory to write HTML, Markdown, CSS, JS, and asset files to
     * @param editBaseUrl the base URL for the Edit button on GitHub
     */
    public static void md2html(Path inputDirectory, Path outputDirectory, String editBaseUrl) {
        Map<Path, String> inputMarkdown = readMarkdownFiles(requireDirectory(inputDirectory));
        Map<Path, String> withIndexes = INDEX_GENERATOR.generate(inputDirectory, inputMarkdown);
        Map<Path, String> resolved = MAGIC_LINK_RESOLVER.resolve(withIndexes);
        HTML.generate(resolved, inputDirectory, outputDirectory, editBaseUrl);
    }

    private static Map<Path, String> readMarkdownFiles(Path inputDirectory) {
        Map<Path, String> map = new LinkedHashMap<>();
        try (Stream<Path> stream = Files.walk(inputDirectory)) {
            List<Path> files =
                    stream.filter(Files::isRegularFile)
                            .filter(p -> !isIgnored(inputDirectory, p))
                            .filter(Markdown::isMarkdown)
                            .sorted()
                            .toList();
            for (Path file : files) {
                Path rel = inputDirectory.relativize(file);
                map.put(rel, Files.readString(file));
            }
            return map;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static boolean isMarkdown(Path path) {
        return MediaTypeDetector.detectOptional(path).map(MediaTypes::isMarkdown).orElse(false);
    }
}
