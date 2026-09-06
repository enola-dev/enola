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

import static dev.enola.common.io.file.FilePaths.copyDirectory;

import com.google.common.html.HtmlEscapers;
import com.google.common.io.Resources;

import dev.enola.common.MoreStrings;
import dev.enola.common.io.classpath.MoreResources;
import dev.enola.common.io.mediatype.MediaTypeDetector;
import dev.enola.common.io.mediatype.MediaTypes;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Generates HTML from Markdown content or directory trees of Markdown files. */
class HtmlGenerator {

    private static final HtmlRenderer RENDERER =
            HtmlRenderer.builder()
                    .extensions(Markdown.EXTENSIONS)
                    .nodeRendererFactory(MermaidNodeRenderer::new)
                    .nodeRendererFactory(MagicLinkHtmlNodeRenderer::new)
                    .build();

    static class MagicLinkHtmlNodeRenderer implements NodeRenderer {
        private final HtmlWriter html;

        MagicLinkHtmlNodeRenderer(HtmlNodeRendererContext context) {
            this.html = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(MagicLinkNode.class);
        }

        @Override
        public void render(Node node) {
            if (node instanceof MagicLinkNode magicLinkNode) {
                html.text("[[" + magicLinkNode.getRaw() + "]]");
            }
        }
    }

    static class MermaidNodeRenderer implements NodeRenderer {
        private final HtmlNodeRendererContext context;
        private final HtmlWriter html;

        MermaidNodeRenderer(HtmlNodeRendererContext context) {
            this.context = context;
            this.html = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(FencedCodeBlock.class);
        }

        @Override
        public void render(Node node) {
            if (node instanceof FencedCodeBlock fencedCodeBlock) {
                String literal = fencedCodeBlock.getLiteral();
                String info = fencedCodeBlock.getInfo();
                String language = "";
                if (info != null && !info.isEmpty()) {
                    int space = info.indexOf(" ");
                    language = space == -1 ? info : info.substring(0, space);
                }
                if ("mermaid".equalsIgnoreCase(language)) {
                    html.line();
                    html.tag(
                            "pre",
                            context.extendAttributes(node, "pre", Map.of("class", "mermaid")));
                    html.text(literal);
                    html.tag("/pre");
                    html.line();
                    return;
                }

                Map<String, String> attributes = new LinkedHashMap<>();
                if (!language.isEmpty()) {
                    attributes.put("class", "language-" + language);
                }
                html.line();
                html.tag("pre", context.extendAttributes(node, "pre", Map.of()));
                html.tag("code", context.extendAttributes(node, "code", attributes));
                html.text(literal);
                html.tag("/code");
                html.tag("/pre");
                html.line();
            }
        }
    }

    /**
     * Walks {@code inputDirectory} and writes converted {@code *.html} files, resolved {@code *.md}
     * files, {@code wiki.css}, {@code wiki.js}, and copied assets to {@code outputDirectory}.
     *
     * @param inputDirectory the directory containing Markdown and asset files
     * @param outputDirectory the directory to write HTML, Markdown, CSS, JS, and asset files to
     * @param editBaseUrl the base URL for the Edit button on GitHub
     */
    public void generate(Path inputDirectory, Path outputDirectory, String editBaseUrl) {
        if (!Files.isDirectory(inputDirectory)) {
            throw new IllegalArgumentException("Not a directory: " + inputDirectory);
        }
        try {
            Map<Path, String> markdownFiles = new LinkedHashMap<>();
            try (Stream<Path> stream = Files.walk(inputDirectory)) {
                List<Path> files =
                        stream.filter(Files::isRegularFile)
                                .filter(p -> !isIgnored(inputDirectory, p))
                                .filter(HtmlGenerator::isMarkdown)
                                .toList();
                for (Path file : files) {
                    markdownFiles.put(inputDirectory.relativize(file), Files.readString(file));
                }
            }
            generate(markdownFiles, inputDirectory, outputDirectory, editBaseUrl);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes converted {@code *.html} files and resolved {@code *.md} files for each entry in
     * {@code markdownFiles}, writes {@code wiki.css} and {@code wiki.js}, and copies non-Markdown
     * asset files from {@code inputDirectory} to {@code outputDirectory}.
     *
     * @param markdownFiles map of relative paths to Markdown content
     * @param inputDirectory the directory containing asset files to copy
     * @param outputDirectory the directory to write HTML, Markdown, CSS, JS, and asset files to
     * @param editBaseUrl the base URL for the Edit button on GitHub
     */
    public void generate(
            Map<Path, String> markdownFiles,
            Path inputDirectory,
            Path outputDirectory,
            String editBaseUrl) {
        try {
            Files.createDirectories(outputDirectory);
            copyWebAssets(outputDirectory);

            for (var entry : markdownFiles.entrySet()) {
                Path relPath = entry.getKey();
                String markdown = entry.getValue();
                String fileName = relPath.getFileName().toString();
                String baseName = getNameWithoutExtension(fileName);
                Path parent = relPath.getParent();
                Path htmlRelPath =
                        parent != null
                                ? parent.resolve(baseName + ".html")
                                : Path.of(baseName + ".html");
                Path destPath = outputDirectory.resolve(htmlRelPath);
                if (destPath.getParent() != null) {
                    Files.createDirectories(destPath.getParent());
                }

                Path mdDestPath = outputDirectory.resolve(relPath);
                if (mdDestPath.getParent() != null) {
                    Files.createDirectories(mdDestPath.getParent());
                }
                Files.writeString(mdDestPath, markdown);

                Node document = parseAndTransform(markdown);
                String title = extractTitle(document);
                if (title == null || title.isBlank()) {
                    title = baseName;
                }
                int depth = parent != null ? parent.getNameCount() : 0;
                String cssHref = (depth > 0 ? "../".repeat(depth) : "") + "wiki.css";
                String jsHref = (depth > 0 ? "../".repeat(depth) : "") + "wiki.js";
                String mdHref = fileName;
                String editUrl = resolveEditUrl(editBaseUrl, relPath, inputDirectory);
                String breadcrumbsHtml = generateBreadcrumbs(relPath, markdownFiles);
                String html =
                        renderPage(
                                document, title, cssHref, jsHref, editUrl, breadcrumbsHtml, mdHref);
                Files.writeString(destPath, html);
            }

            copyDirectory(
                    inputDirectory,
                    outputDirectory,
                    p -> !isIgnored(inputDirectory, p) && !isMarkdown(p));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Converts Markdown text to an HTML fragment. */
    public String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Node document = parseAndTransform(markdown);
        return RENDERER.render(document);
    }

    /** Converts Markdown text to a complete HTML5 page, extracting the title from headings. */
    public String toHtmlPage(String markdown) {
        return toHtmlPage(markdown, null, null, null, null, null, null);
    }

    /** Converts Markdown text to a complete HTML5 page with the given title. */
    public String toHtmlPage(String markdown, @Nullable String title) {
        return toHtmlPage(markdown, title, null, null, null, null, null);
    }

    /** Converts Markdown text to a complete HTML5 page with title, CSS, and Edit URL. */
    public String toHtmlPage(
            String markdown,
            @Nullable String title,
            @Nullable String cssHref,
            @Nullable String editUrl) {
        String jsHref = deriveJsHref(cssHref);
        return toHtmlPage(markdown, title, cssHref, jsHref, editUrl, null, null);
    }

    /**
     * Converts Markdown text to a complete HTML5 page with title, CSS, Edit URL, and breadcrumbs.
     */
    public String toHtmlPage(
            String markdown,
            @Nullable String title,
            @Nullable String cssHref,
            @Nullable String editUrl,
            @Nullable String breadcrumbsHtml) {
        String jsHref = deriveJsHref(cssHref);
        return toHtmlPage(markdown, title, cssHref, jsHref, editUrl, breadcrumbsHtml, null);
    }

    /**
     * Converts Markdown text to a complete HTML5 page with title, CSS, Edit URL, breadcrumbs, and
     * Markdown link.
     */
    public String toHtmlPage(
            String markdown,
            @Nullable String title,
            @Nullable String cssHref,
            @Nullable String editUrl,
            @Nullable String breadcrumbsHtml,
            @Nullable String mdHref) {
        String jsHref = deriveJsHref(cssHref);
        return toHtmlPage(markdown, title, cssHref, jsHref, editUrl, breadcrumbsHtml, mdHref);
    }

    /**
     * Converts Markdown text to a complete HTML5 page with title, CSS, JS, Edit URL, breadcrumbs,
     * and Markdown link.
     */
    public String toHtmlPage(
            String markdown,
            @Nullable String title,
            @Nullable String cssHref,
            @Nullable String jsHref,
            @Nullable String editUrl,
            @Nullable String breadcrumbsHtml,
            @Nullable String mdHref) {
        if (markdown == null || markdown.isEmpty()) {
            return buildPageHtml(
                    "",
                    title != null ? title : "",
                    cssHref,
                    jsHref,
                    editUrl,
                    breadcrumbsHtml,
                    mdHref);
        }
        Node document = parseAndTransform(markdown);
        String effectiveTitle = title;
        if (effectiveTitle == null || effectiveTitle.isBlank()) {
            effectiveTitle = extractTitle(document);
        }
        return renderPage(
                document,
                effectiveTitle != null ? effectiveTitle : "",
                cssHref,
                jsHref,
                editUrl,
                breadcrumbsHtml,
                mdHref);
    }

    private static @Nullable String deriveJsHref(@Nullable String cssHref) {
        if (cssHref == null) {
            return null;
        }
        if (cssHref.endsWith(".css")) {
            return cssHref.substring(0, cssHref.length() - 4) + ".js";
        }
        return cssHref;
    }

    private Node parseAndTransform(String markdown) {
        Node document = Markdown.PARSER.parse(markdown);
        document.accept(
                new AbstractVisitor() {
                    @Override
                    public void visit(Link link) {
                        String destination = link.getDestination();
                        if (destination != null) {
                            link.setDestination(rewriteLinkDestination(destination));
                        }
                        super.visit(link);
                    }
                });
        return document;
    }

    private String renderPage(
            Node document,
            String title,
            @Nullable String cssHref,
            @Nullable String jsHref,
            @Nullable String editUrl,
            @Nullable String breadcrumbsHtml,
            @Nullable String mdHref) {
        String bodyHtml = RENDERER.render(document);
        return buildPageHtml(bodyHtml, title, cssHref, jsHref, editUrl, breadcrumbsHtml, mdHref);
    }

    private String buildPageHtml(
            String bodyHtml,
            String title,
            @Nullable String cssHref,
            @Nullable String jsHref,
            @Nullable String editUrl,
            @Nullable String breadcrumbsHtml,
            @Nullable String mdHref) {
        String escapedTitle = HtmlEscapers.htmlEscaper().escape(title);
        var sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("<title>").append(escapedTitle).append("</title>\n");
        if (mdHref != null && !mdHref.isEmpty()) {
            sb.append("<link rel=\"alternate\" type=\"text/markdown\" href=\"")
                    .append(HtmlEscapers.htmlEscaper().escape(mdHref))
                    .append("\">\n");
        }
        if (cssHref != null && !cssHref.isEmpty()) {
            sb.append("<link rel=\"stylesheet\" href=\"")
                    .append(HtmlEscapers.htmlEscaper().escape(cssHref))
                    .append("\">\n");
        }
        if (jsHref != null && !jsHref.isEmpty()) {
            sb.append("<script type=\"module\" src=\"")
                    .append(HtmlEscapers.htmlEscaper().escape(jsHref))
                    .append("\"></script>\n");
            sb.append("<script>\n");
            sb.append("  if (location.protocol === 'file:') {\n");
            sb.append("    window.addEventListener('DOMContentLoaded', () => {\n");
            sb.append("      document.querySelectorAll('pre.mermaid').forEach(el => {\n");
            sb.append("        const msg = document.createElement('p');\n");
            sb.append("        msg.className = 'mermaid-file-warning';\n");
            sb.append(
                    "        msg.textContent = 'Please serve this page over HTTP instead of"
                            + " file://';\n");
            sb.append("        el.replaceWith(msg);\n");
            sb.append("      });\n");
            sb.append("    });\n");
            sb.append("  }\n");
            sb.append("</script>\n");
        }
        sb.append("</head>\n");
        sb.append("<body>\n");
        if ((editUrl != null && !editUrl.isEmpty()) || (mdHref != null && !mdHref.isEmpty())) {
            sb.append("<div class=\"header-actions\">\n");
            if (mdHref != null && !mdHref.isEmpty()) {
                sb.append("  <a class=\"action-button md-button\" href=\"")
                        .append(HtmlEscapers.htmlEscaper().escape(mdHref))
                        .append("\">Markdown</a>\n");
            }
            if (editUrl != null && !editUrl.isEmpty()) {
                sb.append("  <a class=\"action-button edit-button\" href=\"")
                        .append(HtmlEscapers.htmlEscaper().escape(editUrl))
                        .append("\" target=\"_blank\">Edit</a>\n");
            }
            sb.append("</div>\n");
        }
        if (breadcrumbsHtml != null && !breadcrumbsHtml.isEmpty()) {
            sb.append(breadcrumbsHtml).append("\n");
        }
        sb.append(bodyHtml);
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }

    static String resolveEditUrl(String editBaseUrl, Path relPath, Path inputDirectory) {
        String fileName = relPath.getFileName().toString();
        if (!"index.md".equalsIgnoreCase(fileName)) {
            return joinUrl(editBaseUrl, relPath.toString());
        }

        Path relDir = relPath.getParent() != null ? relPath.getParent() : Path.of("");
        Path readmeSource = relDir.resolve("README.md");
        if (Files.isRegularFile(inputDirectory.resolve(readmeSource))) {
            return joinUrl(editBaseUrl, readmeSource.toString());
        }

        Path indexSource = relDir.resolve("index.md");
        if (Files.isRegularFile(inputDirectory.resolve(indexSource))) {
            return joinUrl(editBaseUrl, indexSource.toString());
        }

        // Neither README.md nor index.md exists at source: link to GitHub create new file
        String newBase = editBaseUrl.replaceFirst("/edit/", "/new/");
        if (relDir.toString().isEmpty()) {
            return trimTrailingSlashes(newBase) + "?filename=README.md";
        } else {
            return joinUrl(newBase, relDir.toString()) + "?filename=README.md";
        }
    }

    static String joinUrl(String baseUrl, String relativePath) {
        String normalizedRel = relativePath.replace('\\', '/');
        if (baseUrl.endsWith("/")) {
            return baseUrl
                    + (normalizedRel.startsWith("/") ? normalizedRel.substring(1) : normalizedRel);
        }
        return baseUrl + (normalizedRel.startsWith("/") ? normalizedRel : "/" + normalizedRel);
    }

    private static String trimTrailingSlashes(String url) {
        int i = url.length();
        while (i > 0 && url.charAt(i - 1) == '/') {
            i--;
        }
        return url.substring(0, i);
    }

    static String rewriteLinkDestination(String destination) {
        if (destination.isEmpty()
                || destination.startsWith("http://")
                || destination.startsWith("https://")
                || destination.startsWith("mailto:")
                || destination.startsWith("data:")
                || destination.startsWith("javascript:")
                || destination.startsWith("#")
                || destination.startsWith("//")) {
            return destination;
        }
        int hashIndex = destination.indexOf('#');
        String base = hashIndex >= 0 ? destination.substring(0, hashIndex) : destination;
        String anchor = hashIndex >= 0 ? destination.substring(hashIndex) : "";

        int queryIndex = base.indexOf('?');
        String path = queryIndex >= 0 ? base.substring(0, queryIndex) : base;
        String query = queryIndex >= 0 ? base.substring(queryIndex) : "";

        if (path.endsWith(".md")) {
            return path.substring(0, path.length() - 3) + ".html" + query + anchor;
        } else if (path.endsWith(".markdown")) {
            return path.substring(0, path.length() - 9) + ".html" + query + anchor;
        }
        return destination;
    }

    static @Nullable String generateBreadcrumbs(Path relPath, Map<Path, String> markdownFiles) {
        Path parent = relPath.getParent();
        if (parent == null) {
            return null;
        }

        String fileName = relPath.getFileName().toString();
        boolean isIndex =
                "index.md".equalsIgnoreCase(fileName) || "README.md".equalsIgnoreCase(fileName);

        List<Path> dirHierarchy = new ArrayList<>();
        Path curr = null;
        for (Path segment : parent) {
            curr = (curr == null) ? segment : curr.resolve(segment);
            dirHierarchy.add(curr);
        }

        if (isIndex) {
            dirHierarchy = dirHierarchy.subList(0, dirHierarchy.size() - 1);
        }

        int depth = parent.getNameCount();
        var sb = new StringBuilder();
        sb.append("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">\n");

        // Root ("Home") breadcrumb
        String rootHref = "../".repeat(depth) + "index.html";
        sb.append("  <a href=\"")
                .append(HtmlEscapers.htmlEscaper().escape(rootHref))
                .append("\">Home</a>\n");

        for (int i = 0; i < dirHierarchy.size(); i++) {
            sb.append("  <span class=\"breadcrumb-separator\">&gt;</span>\n");
            int stepsUp = depth - 1 - i;
            String href = (stepsUp > 0 ? "../".repeat(stepsUp) : "") + "index.html";
            String title = resolveCategoryTitle(dirHierarchy.get(i), markdownFiles);
            sb.append("  <a href=\"")
                    .append(HtmlEscapers.htmlEscaper().escape(href))
                    .append("\">")
                    .append(HtmlEscapers.htmlEscaper().escape(title))
                    .append("</a>\n");
        }
        sb.append("</nav>");
        return sb.toString();
    }

    static String resolveCategoryTitle(Path categoryDir, Map<Path, String> markdownFiles) {
        String dirName =
                categoryDir.getFileName() != null ? categoryDir.getFileName().toString() : "";
        Path leadFile = categoryDir.resolve(dirName + ".md");
        String leadContent = markdownFiles.get(leadFile);
        if (leadContent != null) {
            String title = extractTitle(leadContent);
            if (title != null && !title.isBlank()) {
                return title;
            }
        }
        Path indexFile = categoryDir.resolve("index.md");
        String indexContent = markdownFiles.get(indexFile);
        if (indexContent != null) {
            String title = extractTitle(indexContent);
            if (title != null && !title.isBlank()) {
                return title;
            }
        }
        Path readmeFile = categoryDir.resolve("README.md");
        String readmeContent = markdownFiles.get(readmeFile);
        if (readmeContent != null) {
            String title = extractTitle(readmeContent);
            if (title != null && !title.isBlank()) {
                return title;
            }
        }
        return MoreStrings.toTitleCase(dirName);
    }

    private static @Nullable String extractTitle(String markdownContent) {
        if (markdownContent == null || markdownContent.isBlank()) {
            return null;
        }
        Node document = Markdown.PARSER.parse(markdownContent);
        return extractTitle(document);
    }

    private static @Nullable String extractTitle(Node document) {
        String frontMatterTitle = FrontMatter.from(document).title();
        if (frontMatterTitle != null) {
            return frontMatterTitle;
        }

        for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Heading heading) {
                var sb = new StringBuilder();
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
                            public void visit(HtmlInline htmlInline) {
                                sb.append(htmlInline.getLiteral());
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
        return null;
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

    private static boolean isMarkdown(Path path) {
        return MediaTypeDetector.detectOptional(path)
                .map(MediaTypes::isMarkdown)
                .orElseGet(
                        () -> {
                            var fileName = path.getFileName();
                            if (fileName == null) {
                                return false;
                            }
                            String name = fileName.toString();
                            return name.endsWith(".md") || name.endsWith(".markdown");
                        });
    }

    private static void copyWebAssets(Path outputDirectory) throws IOException {
        List<String> webResources = MoreResources.listResources("web/");
        for (String resource : webResources) {
            String relativeName = resource.substring("web/".length());
            if (!relativeName.isEmpty()) {
                byte[] bytes = Resources.toByteArray(Resources.getResource(resource));
                Path dest = outputDirectory.resolve(relativeName);
                if (dest.getParent() != null) {
                    Files.createDirectories(dest.getParent());
                }
                Files.write(dest, bytes);
            }
        }
    }
}
