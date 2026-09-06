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

import dev.enola.common.MoreStrings;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Generates and updates {@code index.md} files for markdown directory hierarchies according to the
 * Open Knowledge Format (OKF) index specification and wiki maintenance conventions.
 */
class IndexGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(IndexGenerator.class);

    private static final Pattern SUBCATEGORIES_HEADING_PATTERN =
            Pattern.compile("(?m)^##\\s+Subcategories\\s*$");

    private static final Pattern ARTICLES_HEADING_PATTERN =
            Pattern.compile("(?m)^##\\s+Articles\\s*$");

    private static final Pattern ANY_HEADING_PATTERN = Pattern.compile("(?m)^#{1,6}\\s+");

    /** Walks {@code rootDirectory} and creates or updates {@code index.md} files. */
    void generate(Path rootDirectory) {
        if (!Files.isDirectory(rootDirectory)) {
            throw new IllegalArgumentException("Not a directory: " + rootDirectory);
        }
        try {
            Map<Path, String> inputMarkdown = new LinkedHashMap<>();
            try (Stream<Path> stream = Files.walk(rootDirectory)) {
                List<Path> files =
                        stream.filter(Files::isRegularFile)
                                .filter(p -> !isIgnored(rootDirectory, p))
                                .filter(Markdown::isMarkdown)
                                .toList();
                for (Path f : files) {
                    inputMarkdown.put(rootDirectory.relativize(f), Files.readString(f));
                }
            }
            Map<Path, String> generated = generate(rootDirectory, inputMarkdown);
            for (var entry : generated.entrySet()) {
                if ("index.md".equalsIgnoreCase(entry.getKey().getFileName().toString())) {
                    Path dest = rootDirectory.resolve(entry.getKey());
                    String newContent = entry.getValue();
                    if (!Files.exists(dest) || !Files.readString(dest).equals(newContent)) {
                        LOG.info("Creating or updating {}", dest);
                        if (dest.getParent() != null) {
                            Files.createDirectories(dest.getParent());
                        }
                        Files.writeString(dest, newContent);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Generates or updates {@code index.md} files in memory for {@code rootDirectory}.
     *
     * @param rootDirectory the root directory for category naming and directory hierarchy
     * @param markdownPaths relative paths to Markdown content
     * @return all original Markdown files plus all generated/updated {@code index.md} files
     */
    private static Set<Path> collectAllDirectories(Path rootDirectory, Set<Path> markdownPaths) {
        Set<Path> allDirs = new LinkedHashSet<>();
        allDirs.add(Path.of(""));

        if (Files.isDirectory(rootDirectory)) {
            try (Stream<Path> stream = Files.walk(rootDirectory)) {
                stream.filter(Files::isDirectory)
                        .filter(p -> !isIgnored(rootDirectory, p))
                        .map(rootDirectory::relativize)
                        .forEach(allDirs::add);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        for (Path mdPath : markdownPaths) {
            Path parent = mdPath.getParent();
            while (parent != null) {
                allDirs.add(parent);
                parent = parent.getParent();
            }
        }
        return allDirs;
    }

    private static List<Path> sortDirectoriesByDepthDescending(Set<Path> allDirs) {
        List<Path> sortedDirs = new ArrayList<>(allDirs);
        sortedDirs.sort(
                (a, b) -> {
                    int depthA = a.toString().isEmpty() ? 0 : a.getNameCount();
                    int depthB = b.toString().isEmpty() ? 0 : b.getNameCount();
                    if (depthA != depthB) {
                        return Integer.compare(depthB, depthA); // Process deeper directories first
                    }
                    return a.compareTo(b);
                });
        return sortedDirs;
    }

    private Function<Path, @Nullable String> createDescriptionProvider(
            Path rootDirectory, Map<Path, String> result) {
        return relPath -> {
            String content = result.get(relPath);
            if (content != null) {
                return extractDescription(content);
            }
            if (Files.isDirectory(rootDirectory)) {
                Path full = rootDirectory.resolve(relPath);
                return extractDescription(full);
            }
            return null;
        };
    }

    private void processDirectory(
            Path relDir,
            Path rootDirectory,
            Set<Path> allDirs,
            Map<Path, String> inputMarkdown,
            Map<Path, String> result,
            Function<Path, @Nullable String> descriptionProvider) {
        Path indexPath = findFile(relDir, "index.md", inputMarkdown.keySet());
        Path readmePath = findFile(relDir, "README.md", inputMarkdown.keySet());

        if (indexPath != null && readmePath != null) {
            String dirDisplay = relDir.toString().isEmpty() ? "<root>" : relDir.toString();
            throw new IllegalArgumentException(
                    "Directory cannot contain both index.md and README.md: " + dirDisplay);
        }

        Path sourceIndexPath = indexPath != null ? indexPath : readmePath;
        String existingIndex = sourceIndexPath != null ? inputMarkdown.get(sourceIndexPath) : null;

        if (readmePath != null) {
            result.remove(readmePath);
        }

        List<String> childDirs = findChildDirs(relDir, allDirs);
        List<String> mdFileSlugs = findMdFileSlugs(relDir, inputMarkdown.keySet());

        Path relDestIndexPath =
                relDir.toString().isEmpty() ? Path.of("index.md") : relDir.resolve("index.md");

        if (existingIndex != null) {
            String updated =
                    updateIndex(
                            relDir,
                            rootDirectory,
                            existingIndex,
                            childDirs,
                            mdFileSlugs,
                            descriptionProvider);
            result.put(relDestIndexPath, updated);
        } else {
            String created =
                    createNewIndex(
                            relDir, rootDirectory, childDirs, mdFileSlugs, descriptionProvider);
            result.put(relDestIndexPath, created);
        }
    }

    public Map<Path, String> generate(Path rootDirectory, Map<Path, String> inputMarkdown) {
        Map<Path, String> result = new LinkedHashMap<>(inputMarkdown);
        Set<Path> allDirs = collectAllDirectories(rootDirectory, inputMarkdown.keySet());
        List<Path> sortedDirs = sortDirectoriesByDepthDescending(allDirs);
        Function<Path, @Nullable String> descriptionProvider =
                createDescriptionProvider(rootDirectory, result);

        for (Path relDir : sortedDirs) {
            processDirectory(
                    relDir, rootDirectory, allDirs, inputMarkdown, result, descriptionProvider);
        }

        return result;
    }

    private static @Nullable Path findFile(Path relDir, String fileName, Set<Path> paths) {
        for (Path p : paths) {
            Path parent = p.getParent() != null ? p.getParent() : Path.of("");
            if (parent.equals(relDir) && p.getFileName().toString().equalsIgnoreCase(fileName)) {
                return p;
            }
        }
        return null;
    }

    private static List<String> findChildDirs(Path relDir, Set<Path> allDirs) {
        List<String> list = new ArrayList<>();
        int parentDepth = relDir.toString().isEmpty() ? 0 : relDir.getNameCount();
        for (Path d : allDirs) {
            if (d.toString().isEmpty()) {
                continue;
            }
            if (d.getNameCount() == parentDepth + 1
                    && (relDir.toString().isEmpty() || d.startsWith(relDir))) {
                list.add(d.getFileName().toString());
            }
        }
        list.sort(String::compareTo);
        return list;
    }

    private static List<String> findMdFileSlugs(Path relDir, Set<Path> mdFiles) {
        List<String> list = new ArrayList<>();
        for (Path p : mdFiles) {
            Path parent = p.getParent() != null ? p.getParent() : Path.of("");
            if (parent.equals(relDir)) {
                String fileName = p.getFileName().toString();
                if (fileName.toLowerCase(Locale.ROOT).endsWith(".md")
                        && !"index.md".equalsIgnoreCase(fileName)
                        && !"readme.md".equalsIgnoreCase(fileName)) {
                    list.add(fileName.substring(0, fileName.length() - 3));
                }
            }
        }
        list.sort(String::compareTo);
        return list;
    }

    private static boolean isIgnored(Path root, Path path) {
        Path rel = root.relativize(path);
        for (Path segment : rel) {
            if (segment.toString().startsWith(".")) {
                return true;
            }
        }
        return false;
    }

    private String createNewIndex(
            Path relDir,
            Path rootDirectory,
            List<String> childDirs,
            List<String> mdFileSlugs,
            Function<Path, @Nullable String> descriptionProvider) {
        String categoryName = formatCategoryName(relDir, rootDirectory);
        var sb = new StringBuilder();
        sb.append("# ").append(categoryName).append("\n\n");

        String leadSlug = findLeadSlug(relDir, rootDirectory, mdFileSlugs);
        if (leadSlug != null) {
            sb.append(formatLeadItem(relDir, leadSlug, descriptionProvider)).append("\n");
        }

        if (!childDirs.isEmpty()) {
            sb.append("## Subcategories\n\n");
            for (var d : childDirs) {
                sb.append(formatSubcategoryItem(relDir, d, descriptionProvider));
            }
            sb.append("\n");
        }

        List<String> articleSlugs =
                leadSlug != null
                        ? mdFileSlugs.stream().filter(s -> !s.equals(leadSlug)).toList()
                        : mdFileSlugs;

        if (!articleSlugs.isEmpty()) {
            sb.append("## Articles\n\n");
            for (var slug : articleSlugs) {
                sb.append(formatArticleItem(relDir, slug, descriptionProvider));
            }
            sb.append("\n");
        }

        return sb.toString().stripTrailing() + "\n";
    }

    private record SubcategoriesUpdate(String content, List<String> missing) {}

    private record ArticlesUpdate(String content, List<String> missing) {}

    private static String removeLeadAndPreamble(
            Path relDir,
            String content,
            String leadSlug,
            Function<Path, @Nullable String> descriptionProvider) {
        Pattern articleLeadPattern =
                Pattern.compile(
                        "(?m)^[ \\t]*[-*][ \\t]+\\[\\["
                                + Pattern.quote(leadSlug)
                                + "\\]\\](?:[ \\t]*-[ \\t]*[^\\r\\n]*)?[ \\t]*\\r?\\n?");
        String updated = articleLeadPattern.matcher(content).replaceAll("");
        return updateLeadInPreamble(relDir, updated, leadSlug, descriptionProvider);
    }

    private static @Nullable String resolveSubcategoryDescription(
            Path relDir, String d, Function<Path, @Nullable String> descriptionProvider) {
        Path childDirRel = relDir.toString().isEmpty() ? Path.of(d) : relDir.resolve(d);
        String desc = descriptionProvider.apply(childDirRel.resolve(d + ".md"));
        if (desc == null || desc.isEmpty()) {
            desc = descriptionProvider.apply(childDirRel.resolve("index.md"));
        }
        if (desc == null || desc.isEmpty()) {
            desc = descriptionProvider.apply(childDirRel.resolve("README.md"));
        }
        return desc;
    }

    private SubcategoriesUpdate updateSubcategories(
            Path relDir,
            String content,
            List<String> childDirs,
            Function<Path, @Nullable String> descriptionProvider) {
        String updated = content;
        List<String> missing = new ArrayList<>();
        for (var d : childDirs) {
            String desc = resolveSubcategoryDescription(relDir, d, descriptionProvider);
            Pattern subcatPattern =
                    Pattern.compile(
                            "(?m)^([ \\t]*[-*][ \\t]+\\[\\["
                                    + Pattern.quote(d)
                                    + "/index\\]\\])(?:([ \\t]*-[ \\t]*[^\\r\\n]*)|[ \\t]*)$");
            Matcher matcher = subcatPattern.matcher(updated);
            if (matcher.find()) {
                String replacement =
                        desc != null && !desc.isEmpty()
                                ? "$1 - " + Matcher.quoteReplacement(desc)
                                : "$1";
                updated = matcher.replaceAll(replacement);
            } else if (!isSubcategoryLinked(updated, d)) {
                missing.add(d);
            }
        }
        return new SubcategoriesUpdate(updated, missing);
    }

    private ArticlesUpdate updateArticles(
            Path relDir,
            String content,
            List<String> articleSlugs,
            Function<Path, @Nullable String> descriptionProvider) {
        String updated = content;
        List<String> missing = new ArrayList<>();
        for (var slug : articleSlugs) {
            Path article =
                    relDir.toString().isEmpty()
                            ? Path.of(slug + ".md")
                            : relDir.resolve(slug + ".md");
            String desc = descriptionProvider.apply(article);
            Pattern articlePattern =
                    Pattern.compile(
                            "(?m)^([ \\t]*[-*][ \\t]+\\[\\["
                                    + Pattern.quote(slug)
                                    + "\\]\\])(?:([ \\t]*-[ \\t]*[^\\r\\n]*)|[ \\t]*)$");
            Matcher matcher = articlePattern.matcher(updated);
            if (matcher.find()) {
                String replacement =
                        desc != null && !desc.isEmpty()
                                ? "$1 - " + Matcher.quoteReplacement(desc)
                                : "$1";
                updated = matcher.replaceAll(replacement);
            } else if (!isArticleLinked(updated, slug)) {
                missing.add(slug);
            }
        }
        return new ArticlesUpdate(updated, missing);
    }

    private String updateIndex(
            Path relDir,
            Path rootDirectory,
            String existingContent,
            List<String> childDirs,
            List<String> mdFileSlugs,
            Function<Path, @Nullable String> descriptionProvider) {
        String updated = existingContent;
        String leadSlug = findLeadSlug(relDir, rootDirectory, mdFileSlugs);

        if (leadSlug != null) {
            updated = removeLeadAndPreamble(relDir, updated, leadSlug, descriptionProvider);
        }

        var subcatUpdate = updateSubcategories(relDir, updated, childDirs, descriptionProvider);
        updated = subcatUpdate.content();

        List<String> articleSlugs =
                leadSlug != null
                        ? mdFileSlugs.stream().filter(s -> !s.equals(leadSlug)).toList()
                        : mdFileSlugs;

        var articlesUpdate = updateArticles(relDir, updated, articleSlugs, descriptionProvider);
        updated = articlesUpdate.content();

        if (!subcatUpdate.missing().isEmpty()) {
            updated =
                    insertSubcategories(
                            relDir, updated, subcatUpdate.missing(), descriptionProvider);
        }
        if (!articlesUpdate.missing().isEmpty()) {
            updated =
                    insertArticles(relDir, updated, articlesUpdate.missing(), descriptionProvider);
        }

        return updated.stripTrailing() + "\n";
    }

    private static @Nullable String findLeadSlug(
            Path relDir, Path rootDirectory, List<String> mdFileSlugs) {
        String dirName;
        if (relDir.toString().isEmpty()) {
            var fileName = rootDirectory.getFileName();
            if (fileName == null) {
                return null;
            }
            dirName = fileName.toString();
        } else {
            var fileName = relDir.getFileName();
            if (fileName == null) {
                return null;
            }
            dirName = fileName.toString();
        }
        for (String slug : mdFileSlugs) {
            if (slug.equalsIgnoreCase(dirName)) {
                return slug;
            }
        }
        return null;
    }

    private static String updateLeadInPreamble(
            Path relDir,
            String content,
            String leadSlug,
            Function<Path, @Nullable String> descriptionProvider) {
        var h1Matcher = Pattern.compile("(?m)^#\\s+[^\\r\\n]+$").matcher(content);
        if (!h1Matcher.find()) {
            return content;
        }
        int h1End = h1Matcher.end();
        int preambleEnd = findSectionEnd(content, h1End);
        String preamble = content.substring(h1End, preambleEnd);
        String leadItem = formatLeadItem(relDir, leadSlug, descriptionProvider).stripTrailing();

        Pattern leadPattern =
                Pattern.compile(
                        "(?m)^([ \\t]*[-*]?[ \\t]*\\[\\["
                                + Pattern.quote(leadSlug)
                                + "\\]\\])(?:([ \\t]*-[ \\t]*[^\\r\\n]*)|[ \\t]*)$");
        Matcher leadMatcher = leadPattern.matcher(preamble);
        if (leadMatcher.find()) {
            String updatedPreamble = leadMatcher.replaceAll(Matcher.quoteReplacement(leadItem));
            return content.substring(0, h1End) + updatedPreamble + content.substring(preambleEnd);
        }

        String beforePreamble = content.substring(0, h1End);
        String afterPreamble = content.substring(preambleEnd);
        String trimmedPreamble = preamble.strip();
        if (trimmedPreamble.isEmpty()) {
            return beforePreamble + "\n\n" + leadItem + "\n\n" + afterPreamble;
        } else {
            return beforePreamble
                    + "\n\n"
                    + trimmedPreamble
                    + "\n\n"
                    + leadItem
                    + "\n\n"
                    + afterPreamble;
        }
    }

    private static String formatLeadItem(
            Path relDir, String leadSlug, Function<Path, @Nullable String> descriptionProvider) {
        Path article =
                relDir.toString().isEmpty()
                        ? Path.of(leadSlug + ".md")
                        : relDir.resolve(leadSlug + ".md");
        String desc = descriptionProvider.apply(article);
        if (desc != null && !desc.isEmpty()) {
            return "[[" + leadSlug + "]] - " + desc + "\n";
        }
        return "[[" + leadSlug + "]]\n";
    }

    private String insertSubcategories(
            Path relDir,
            String content,
            List<String> missingSubcategories,
            Function<Path, @Nullable String> descriptionProvider) {
        var subcatMatcher = SUBCATEGORIES_HEADING_PATTERN.matcher(content);
        if (subcatMatcher.find()) {
            int headingEnd = subcatMatcher.end();
            int sectionEnd = findSectionEnd(content, headingEnd);
            var sb = new StringBuilder();
            for (var d : missingSubcategories) {
                sb.append(formatSubcategoryItem(relDir, d, descriptionProvider));
            }
            return insertIntoSection(content, headingEnd, sectionEnd, sb.toString());
        }

        var articlesMatcher = ARTICLES_HEADING_PATTERN.matcher(content);
        if (articlesMatcher.find()) {
            int articlesStart = articlesMatcher.start();
            var sb = new StringBuilder();
            sb.append("## Subcategories\n\n");
            for (var d : missingSubcategories) {
                sb.append(formatSubcategoryItem(relDir, d, descriptionProvider));
            }
            sb.append("\n");
            return content.substring(0, articlesStart) + sb + content.substring(articlesStart);
        }

        var sb = new StringBuilder(content.stripTrailing());
        sb.append("\n\n## Subcategories\n\n");
        for (var d : missingSubcategories) {
            sb.append(formatSubcategoryItem(relDir, d, descriptionProvider));
        }
        return sb.toString();
    }

    private String insertArticles(
            Path relDir,
            String content,
            List<String> missingArticles,
            Function<Path, @Nullable String> descriptionProvider) {
        var articlesMatcher = ARTICLES_HEADING_PATTERN.matcher(content);
        if (articlesMatcher.find()) {
            int headingEnd = articlesMatcher.end();
            int sectionEnd = findSectionEnd(content, headingEnd);
            var sb = new StringBuilder();
            for (var slug : missingArticles) {
                sb.append(formatArticleItem(relDir, slug, descriptionProvider));
            }
            return insertIntoSection(content, headingEnd, sectionEnd, sb.toString());
        }

        var sb = new StringBuilder(content.stripTrailing());
        sb.append("\n\n## Articles\n\n");
        for (var slug : missingArticles) {
            sb.append(formatArticleItem(relDir, slug, descriptionProvider));
        }
        return sb.toString();
    }

    private String formatSubcategoryItem(
            Path relDir, String childDir, Function<Path, @Nullable String> descriptionProvider) {
        Path childDirRel =
                relDir.toString().isEmpty() ? Path.of(childDir) : relDir.resolve(childDir);
        String desc = descriptionProvider.apply(childDirRel.resolve(childDir + ".md"));
        if (desc == null || desc.isEmpty()) {
            desc = descriptionProvider.apply(childDirRel.resolve("index.md"));
        }
        if (desc != null && !desc.isEmpty()) {
            return "- [[" + childDir + "/index]] - " + desc + "\n";
        }
        return "- [[" + childDir + "/index]]\n";
    }

    private String formatArticleItem(
            Path relDir, String slug, Function<Path, @Nullable String> descriptionProvider) {
        Path article =
                relDir.toString().isEmpty() ? Path.of(slug + ".md") : relDir.resolve(slug + ".md");
        String desc = descriptionProvider.apply(article);
        if (desc != null && !desc.isEmpty()) {
            return "- [[" + slug + "]] - " + desc + "\n";
        }
        return "- [[" + slug + "]]\n";
    }

    private static int findSectionEnd(String content, int fromIndex) {
        var headingMatcher = ANY_HEADING_PATTERN.matcher(content);
        if (headingMatcher.find(fromIndex)) {
            return headingMatcher.start();
        }
        return content.length();
    }

    private static String insertIntoSection(
            String content, int headingEnd, int sectionEnd, String itemsToInsert) {
        String sectionBody = content.substring(headingEnd, sectionEnd);
        int lastNonWs = -1;
        for (int i = sectionBody.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(sectionBody.charAt(i))) {
                lastNonWs = i;
                break;
            }
        }

        if (lastNonWs == -1) {
            return content.substring(0, headingEnd)
                    + "\n\n"
                    + itemsToInsert
                    + content.substring(sectionEnd);
        }

        int nextNewline = sectionBody.indexOf('\n', lastNonWs);
        int insertionOffset;
        if (nextNewline != -1) {
            insertionOffset = headingEnd + nextNewline + 1;
        } else {
            insertionOffset = headingEnd + sectionBody.length();
        }

        String before = content.substring(0, insertionOffset);
        if (!before.endsWith("\n")) {
            before += "\n";
        }
        String after = content.substring(insertionOffset);
        return before + itemsToInsert + after;
    }

    private boolean isSubcategoryLinked(String content, String dirName) {
        return content.contains("[[" + dirName + "/index")
                || content.contains("[[" + dirName + "]]")
                || content.contains("[[" + dirName + "|")
                || content.contains("[[" + dirName + "#")
                || content.contains("(" + dirName + "/index.md)")
                || content.contains("(" + dirName + "/index)")
                || content.contains("(" + dirName + "/)")
                || content.contains("(" + dirName + ")")
                || content.contains(dirName + "/index");
    }

    private boolean isArticleLinked(String content, String slug) {
        return content.contains("[[" + slug + "]]")
                || content.contains("[[" + slug + "|")
                || content.contains("[[" + slug + "#")
                || content.contains("[[" + slug + ".md")
                || content.contains("(" + slug + ".md)")
                || content.contains("(" + slug + ")")
                || content.contains("(" + slug + ".md#")
                || content.contains(slug + ".md");
    }

    @Nullable String extractDescription(Path markdownFile) {
        if (!Files.exists(markdownFile) || !Files.isRegularFile(markdownFile)) {
            return null;
        }
        try {
            String content = Files.readString(markdownFile);
            return extractDescription(content);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable String extractDescription(String markdownContent) {
        if (markdownContent == null || markdownContent.isBlank()) {
            return null;
        }

        Node document = Markdown.PARSER.parse(markdownContent);
        String desc = FrontMatter.from(document).description();
        if (desc != null) {
            return desc;
        }

        boolean foundHeading = false;
        @Nullable String fallbackParagraph = null;
        for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Heading) {
                foundHeading = true;
                continue;
            }
            if (node instanceof Paragraph paragraph) {
                var sb = new StringBuilder();
                paragraph.accept(
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
                            public void visit(SoftLineBreak softLineBreak) {
                                sb.append(' ');
                            }

                            @Override
                            public void visit(HardLineBreak hardLineBreak) {
                                sb.append(' ');
                            }

                            @Override
                            public void visit(CustomNode customNode) {
                                if (customNode instanceof MagicLinkNode magicLink) {
                                    sb.append("[[").append(magicLink.getRaw()).append("]]");
                                }
                                super.visit(customNode);
                            }
                        });
                String text = MoreStrings.normalizeWhitespace(sb.toString());
                if (text != null) {
                    if (foundHeading) {
                        return text;
                    }
                    if (fallbackParagraph == null) {
                        fallbackParagraph = text;
                    }
                }
            }
        }
        return fallbackParagraph;
    }

    private String formatCategoryName(Path relDir, Path rootDirectory) {
        if (relDir.toString().isEmpty() || relDir.equals(Path.of(""))) {
            var fileName = rootDirectory.getFileName();
            if (fileName == null
                    || "docs".equalsIgnoreCase(fileName.toString())
                    || fileName.toString().isEmpty()) {
                return "Knowledge Wiki";
            }
            return MoreStrings.toTitleCase(fileName.toString());
        }
        var fileName = relDir.getFileName();
        if (fileName == null) {
            return "Index";
        }
        return MoreStrings.toTitleCase(fileName.toString());
    }
}
