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

import static com.google.common.io.Files.getNameWithoutExtension;

import dev.enola.common.io.http.client.HttpGetter;
import dev.enola.common.io.mediatype.MediaTypeDetector;
import dev.enola.common.io.mediatype.MediaTypes;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.SourceSpan;
import org.commonmark.node.Text;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// See https://docs.enola.dev/specs/markdown-magic-link (AKA WikiLink)
class MagicLinkResolver {

    // TODO This could be improved to handle anchors smarter
    // "Anchor: [[other#section]]" should use the title of the #section …

    private static final Pattern HTML_TITLE_PATTERN =
            Pattern.compile("(?i)<title[^>]*>(.*?)</title>");

    private final HttpGetter httpGetter;
    private final ConcurrentMap<Path, CompletableFuture<String>> titles = new ConcurrentHashMap<>();

    MagicLinkResolver(HttpGetter httpGetter) {
        this.httpGetter = Objects.requireNonNull(httpGetter, "httpGetter");
    }

    void resolve(Path inputDirectory, Path outputDirectory) {
        try {
            Files.createDirectories(outputDirectory);
            List<Path> allFiles;
            try (Stream<Path> stream = Files.walk(inputDirectory)) {
                allFiles = stream.filter(Files::isRegularFile).toList();
            }

            Function<Path, CompletableFuture<String>> titleProvider =
                    relPath ->
                            titles.computeIfAbsent(
                                    relPath,
                                    p -> {
                                        Path fullPath = inputDirectory.resolve(p);
                                        if (Files.exists(fullPath)
                                                && Files.isRegularFile(fullPath)) {
                                            try {
                                                String content = Files.readString(fullPath);
                                                return CompletableFuture.completedFuture(
                                                        extractTitle(p, content));
                                            } catch (IOException e) {
                                                throw new UncheckedIOException(e);
                                            }
                                        } else {
                                            return CompletableFuture.completedFuture(
                                                    fallbackTitle(p));
                                        }
                                    });

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Path file : allFiles) {
                Path relPath = inputDirectory.relativize(file);
                Path destPath = outputDirectory.resolve(relPath);

                if (isMarkdown(file)) {
                    futures.add(
                            CompletableFuture.runAsync(
                                    () -> {
                                        try {
                                            String content = Files.readString(file);
                                            String replaced =
                                                    resolveLinks(relPath, content, titleProvider);
                                            if (destPath.getParent() != null) {
                                                Files.createDirectories(destPath.getParent());
                                            }
                                            Files.writeString(destPath, replaced);
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e);
                                        }
                                    }));
                } else {
                    futures.add(
                            CompletableFuture.runAsync(
                                    () -> {
                                        try {
                                            if (destPath.getParent() != null) {
                                                Files.createDirectories(destPath.getParent());
                                            }
                                            Files.copy(
                                                    file,
                                                    destPath,
                                                    StandardCopyOption.REPLACE_EXISTING);
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e);
                                        }
                                    }));
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Map<Path, String> resolve(Map<Path, String> inputMarkdown) {
        Function<Path, CompletableFuture<String>> titleProvider =
                targetPath ->
                        titles.computeIfAbsent(
                                targetPath,
                                p -> {
                                    String content = inputMarkdown.get(p);
                                    if (content != null) {
                                        return CompletableFuture.completedFuture(
                                                extractTitle(p, content));
                                    } else {
                                        return CompletableFuture.completedFuture(fallbackTitle(p));
                                    }
                                });

        Map<Path, String> result = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (var entry : inputMarkdown.entrySet()) {
            futures.add(
                    CompletableFuture.runAsync(
                            () -> {
                                String processed =
                                        resolveLinks(
                                                entry.getKey(), entry.getValue(), titleProvider);
                                result.put(entry.getKey(), processed);
                            }));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return result;
    }

    private String resolveLinks(
            Path currentFilePath,
            String content,
            Function<Path, CompletableFuture<String>> titleProvider) {
        Node document = Markdown.PARSER.parse(content);
        List<MagicLinkNode> magicLinks = new ArrayList<>();
        document.accept(
                new AbstractVisitor() {
                    @Override
                    public void visit(CustomNode customNode) {
                        if (customNode instanceof MagicLinkNode magicLinkNode) {
                            magicLinks.add(magicLinkNode);
                        }
                        super.visit(customNode);
                    }
                });

        if (magicLinks.isEmpty()) {
            return content;
        }

        StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        for (MagicLinkNode magicLink : magicLinks) {
            List<SourceSpan> spans = magicLink.getSourceSpans();
            if (spans.isEmpty()) {
                continue;
            }
            SourceSpan firstSpan = spans.get(0);
            SourceSpan lastSpan = spans.get(spans.size() - 1);
            int start = firstSpan.getInputIndex();
            int end = lastSpan.getInputIndex() + lastSpan.getLength();
            if (start > 0 && content.charAt(start - 1) == '[') {
                start = start - 1;
            }
            int closingBracket = content.indexOf("]]", start);
            if (closingBracket != -1 && closingBracket <= end + 10) {
                end = closingBracket + 2;
            }

            sb.append(content, lastIndex, start);

            String replacement = resolveLink(currentFilePath, magicLink.getRaw(), titleProvider);
            sb.append(replacement);

            lastIndex = end;
        }
        sb.append(content, lastIndex, content.length());
        return sb.toString();
    }

    private record ParsedLink(String target, @Nullable String explicitLabel) {}

    private static ParsedLink parseTargetAndLabel(String raw) {
        int escapedPipeIdx = raw.indexOf("\\|");
        String rawTarget;
        String rawLabel;
        if (escapedPipeIdx >= 0) {
            rawTarget = raw.substring(0, escapedPipeIdx).trim();
            rawLabel = raw.substring(escapedPipeIdx + 2).trim();
        } else {
            int pipeIdx = raw.indexOf('|');
            rawTarget = (pipeIdx >= 0 ? raw.substring(0, pipeIdx) : raw).trim();
            rawLabel = pipeIdx >= 0 ? raw.substring(pipeIdx + 1).trim() : null;
        }
        String explicitLabel = (rawLabel != null && !rawLabel.isEmpty()) ? rawLabel : null;
        return new ParsedLink(rawTarget, explicitLabel);
    }

    private String resolveRelativeLink(
            Path currentFilePath,
            String rawTarget,
            @Nullable String explicitLabel,
            Function<Path, CompletableFuture<String>> titleProvider) {
        int hashIdx = rawTarget.indexOf('#');
        String baseTarget = hashIdx >= 0 ? rawTarget.substring(0, hashIdx) : rawTarget;
        String anchor = hashIdx >= 0 ? rawTarget.substring(hashIdx) : "";
        String hrefBase = isMarkdown(baseTarget) ? baseTarget : baseTarget + ".md";
        String href = hrefBase + anchor;
        Path currentDir =
                currentFilePath.getParent() != null ? currentFilePath.getParent() : Path.of("");
        Path targetRelPath = currentDir.resolve(hrefBase).normalize();
        String label =
                explicitLabel != null ? explicitLabel : titleProvider.apply(targetRelPath).join();
        return "[" + label + "](" + href + ")";
    }

    private String resolveLink(
            Path currentFilePath,
            String raw,
            Function<Path, CompletableFuture<String>> titleProvider) {
        var parsed = parseTargetAndLabel(raw);
        String target = parsed.target();
        String explicitLabel = parsed.explicitLabel();

        if (target.startsWith("http://") || target.startsWith("https://")) {
            String label = explicitLabel != null ? explicitLabel : fetchRemoteTitle(target);
            return "[" + label + "](" + target + ")";
        }
        if (target.startsWith("#")) {
            String label = explicitLabel != null ? explicitLabel : target;
            return "[" + label + "](" + target + ")";
        }
        return resolveRelativeLink(currentFilePath, target, explicitLabel, titleProvider);
    }

    private static @Nullable String parseHeading(String line) {
        if (!line.startsWith("#")) {
            return null;
        }
        int hashCount = 0;
        while (hashCount < line.length() && line.charAt(hashCount) == '#') {
            hashCount++;
        }
        if (hashCount < line.length() && Character.isWhitespace(line.charAt(hashCount))) {
            String heading = line.substring(hashCount).trim();
            if (!heading.isEmpty()) {
                return heading;
            }
        }
        return null;
    }

    private static @Nullable String extractMarkdownTitleFromStream(Stream<String> stream) {
        boolean inFrontMatter = false;
        boolean firstNonEmptyLine = true;
        for (Iterator<String> it = stream.iterator(); it.hasNext(); ) {
            String rawLine = it.next();
            String line = rawLine.trim();
            if (firstNonEmptyLine && !line.isEmpty()) {
                firstNonEmptyLine = false;
                if ("---".equals(line)) {
                    inFrontMatter = true;
                    continue;
                }
            }
            if (inFrontMatter) {
                if ("---".equals(line) || "...".equals(line)) {
                    inFrontMatter = false;
                }
                continue;
            }
            String heading = parseHeading(line);
            if (heading != null) {
                return heading;
            }
        }
        return null;
    }

    private static @Nullable String extractHtmlTitleFromStream(Stream<String> stream) {
        StringBuilder headBuffer = new StringBuilder();
        for (Iterator<String> it = stream.iterator(); it.hasNext(); ) {
            String line = it.next();
            headBuffer.append(line).append('\n');
            if (line.toLowerCase(Locale.ROOT).contains("</head>")
                    || line.toLowerCase(Locale.ROOT).contains("</title>")
                    || headBuffer.length() > 65536) {
                break;
            }
        }
        Matcher m = HTML_TITLE_PATTERN.matcher(headBuffer);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    private static @Nullable String extractUnknownTitleFromStream(Stream<String> stream) {
        StringBuilder buffer = new StringBuilder();
        String firstHeading = null;
        boolean inFrontMatter = false;
        boolean firstNonEmptyLine = true;
        for (Iterator<String> it = stream.iterator(); it.hasNext(); ) {
            String line = it.next();
            String trimmed = line.trim();
            if (firstNonEmptyLine && !trimmed.isEmpty()) {
                firstNonEmptyLine = false;
                if ("---".equals(trimmed)) {
                    inFrontMatter = true;
                    continue;
                }
            }
            if (inFrontMatter) {
                if ("---".equals(trimmed) || "...".equals(trimmed)) {
                    inFrontMatter = false;
                }
                continue;
            }
            if (firstHeading == null) {
                firstHeading = parseHeading(trimmed);
            }
            buffer.append(line).append('\n');
            if (buffer.length() > 65536) {
                break;
            }
        }
        Matcher m = HTML_TITLE_PATTERN.matcher(buffer);
        if (m.find()) {
            return m.group(1).trim();
        }
        return firstHeading;
    }

    private String fetchRemoteTitle(String url) {
        try (HttpGetter.Response response = httpGetter.getLines(url)) {
            var mediaType = response.mediaType();
            Stream<String> stream = response.lines();
            if (stream == null) {
                return url;
            }

            String title;
            if (MediaTypes.isMarkdown(mediaType)) {
                title = extractMarkdownTitleFromStream(stream);
            } else if (MediaTypes.isHtml(mediaType)) {
                title = extractHtmlTitleFromStream(stream);
            } else {
                title = extractUnknownTitleFromStream(stream);
            }
            return (title != null && !title.isEmpty()) ? title : url;
        } catch (Exception e) {
            // Fall back to url
            return url;
        }
    }

    private static String extractTitle(Path path, @Nullable String markdown) {
        if (markdown != null && !markdown.isBlank()) {
            Node document = Markdown.PARSER.parse(markdown);
            for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {
                if (node instanceof Heading heading) {
                    StringBuilder sb = new StringBuilder();
                    heading.accept(
                            new AbstractVisitor() {
                                @Override
                                public void visit(Text text) {
                                    sb.append(text.getLiteral());
                                }

                                @Override
                                public void visit(Code code) {
                                    sb.append(code.getLiteral());
                                }

                                @Override
                                public void visit(CustomNode customNode) {
                                    if (customNode instanceof MagicLinkNode magicLink) {
                                        sb.append("[[").append(magicLink.getRaw()).append("]]");
                                    }
                                    super.visit(customNode);
                                }
                            });
                    String title = sb.toString().trim();
                    if (!title.isEmpty()) {
                        return title;
                    }
                }
            }
        }
        return fallbackTitle(path);
    }

    private static String fallbackTitle(Path path) {
        String fileName =
                path.getFileName() != null ? path.getFileName().toString() : path.toString();
        if (isMarkdown(path)) {
            return getNameWithoutExtension(fileName);
        }
        return fileName;
    }

    private static boolean isMarkdown(Path path) {
        return MediaTypeDetector.detectOptional(path).map(MediaTypes::isMarkdown).orElse(false);
    }

    private static boolean isMarkdown(String pathOrUri) {
        return MediaTypeDetector.detectOptional(pathOrUri)
                .map(MediaTypes::isMarkdown)
                .orElse(false);
    }
}
