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
package dev.enola.ai.adk.tool;

import static dev.enola.common.SuccessOrError.error;
import static dev.enola.common.SuccessOrError.success;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.SuccessOrError;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemTools {

    // TODO Enforce root(s); see "root" related TODO in McpLoader

    public Map<String, BaseTool> createToolSet() {
        return ImmutableMap.of(
                "read_file", FunctionTool.create(this, "readFile"),
                "write_file", FunctionTool.create(this, "writeFile"),
                "edit_file", FunctionTool.create(this, "editFile"),
                "search_files", FunctionTool.create(this, "searchFiles"),
                "list_directory", FunctionTool.create(this, "listDirectory"),
                "create_directory", FunctionTool.create(this, "createDirectory"),
                "grep_file", FunctionTool.create(this, "grepFile"));
    }

    @Schema(description = "Reads the entire content of a specified file.")
    public Map<String, ?> readFile(
            @Schema(description = "The path to the file to read.") String path) {
        return Tools.toMap(readFileHelper(path));
    }

    // TODO: add a confirmation step for overwriting files
    // atm this will only write if the files does not exist
    @Schema(description = "Writes content to a file.")
    public Map<String, ?> writeFile(
            @Schema(description = "The path to the file to write.") String path,
            @Schema(description = "The content to write to the file.") String content) {
        return Tools.toMap(writeFileHelper(path, content));
    }

    // TODO: add a confirmation step for editing files
    @Schema(
            description =
                    "Replaces a specific range of lines in a file and returns a git-style diff of"
                            + " the changes.")
    public Map<String, ?> editFile(
            @Schema(description = "The path to the file to edit.") String path,
            @Schema(description = "The 1-based starting line number to replace.") int startLine,
            @Schema(description = "The number of old lines to remove.") int linesToRemove,
            @Schema(description = "The new lines of content to insert.") String newContent) {
        return Tools.toMap(editFileHelper(path, startLine, linesToRemove, newContent));
    }

    @Schema(description = "Recursively searches for files and directories using a glob pattern.")
    public Map<String, ?> searchFiles(
            @Schema(description = "The starting directory for the search.") String startPath,
            @Schema(description = "The glob pattern to match (e.g., '**.java', '*.txt').")
                    String glob) {
        return Tools.toMap(searchFilesHelper(startPath, glob));
    }

    @Schema(
            description =
                    "Given a file system path, lists the contents of a directory with all files and"
                            + " directories and details like size and modification date.")
    public Map<String, ?> listDirectory(
            @Schema(description = "The absolute or relative path of the directory to list.")
                    String path) {
        return Tools.toMap(listDirectoryHelper(path));
    }

    @Schema(description = "Creates a directory, including any necessary parent directories.")
    public Map<String, ?> createDirectory(
            @Schema(description = "The path of the directory to create.") String path) {
        return Tools.toMap(createDirectoryHelper(path));
    }

    @Schema(description = "Searches for a text pattern within a file, like the 'grep' command.")
    public Map<String, ?> grepFile(
            @Schema(description = "The path to the file to search.") String path,
            @Schema(description = "The text or regex pattern to search for.") String pattern,
            @Schema(description = "Number of context lines to show before and after a match.")
                    int context) {
        return Tools.toMap(grepFileHelper(path, pattern, context));
    }

    // Private Helper Methods
    private SuccessOrError<String> readFileHelper(String pathString) {
        try {
            Path path = Paths.get(pathString);
            if (!Files.isReadable(path)) return error("File is not readable: " + path);
            return success(Files.readString(path));
        } catch (IOException e) {
            return error("Failed to read file: " + e.getMessage());
        }
    }

    private SuccessOrError<String> writeFileHelper(String pathString, String content) {
        try {
            Path path = Paths.get(pathString);
            if (Files.exists(path)) return error("File already exists: " + path);
            Files.writeString(
                    path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return success("Successfully wrote " + content.length() + " characters to " + path);
        } catch (IOException e) {
            return error("Failed to write file: " + e.getMessage());
        }
    }

    private SuccessOrError<String> editFileHelper(
            String pathString, int startLine, int linesToRemove, String newContent) {
        try {
            Path path = Paths.get(pathString);
            if (!Files.isRegularFile(path))
                return error("File not found or not a regular file: " + path);

            List<String> originalLines = Files.readAllLines(path);
            if (startLine < 1 || startLine > originalLines.size() + 1)
                return error("Invalid start line number.");

            List<String> newLines = new ArrayList<>(originalLines);
            int startIndex = startLine - 1;
            int end = Math.min(startIndex + linesToRemove, newLines.size());
            if (startIndex < end) {
                newLines.subList(startIndex, end).clear();
            }
            List<String> insertLines =
                    newContent.isEmpty() ? List.of() : Arrays.asList(newContent.split("\n"));
            newLines.addAll(startIndex, insertLines);

            Files.write(path, newLines);

            // Generate Diff
            String diff =
                    generateDiff(
                            pathString,
                            String.join("\n", originalLines),
                            String.join("\n", newLines));
            if (diff.isEmpty()) {
                return success("File edited, but no changes were made.");
            }
            return success("File successfully edited. Diff:\n" + diff);
        } catch (IOException e) {
            return error("Failed to edit file: " + e.getMessage());
        }
    }

    private SuccessOrError<String> searchFilesHelper(String startPath, String glob) {
        Path start = Paths.get(startPath);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        try (Stream<Path> stream = Files.walk(start)) {
            String results =
                    stream.filter(path -> matcher.matches(path.getFileName()))
                            .map(start::relativize)
                            .map(Path::toString)
                            .collect(Collectors.joining("\n"));
            return success(results.isEmpty() ? "No matches found." : "Matches:\n" + results);
        } catch (IOException e) {
            return error("Error searching files: " + e.getMessage());
        }
    }

    private SuccessOrError<String> listDirectoryHelper(String pathString) {
        try (Stream<Path> stream = Files.list(Paths.get(pathString))) {
            String details =
                    stream.filter(
                                    path ->
                                            !path.getFileName()
                                                    .toString()
                                                    .startsWith(".")) // Exclude hidden files
                            .sorted(FileSystemTools::sortPaths)
                            .map(
                                    path -> {
                                        try {
                                            BasicFileAttributes attrs =
                                                    Files.readAttributes(
                                                            path, BasicFileAttributes.class);
                                            String type = attrs.isDirectory() ? "d" : "-";
                                            long size = attrs.size();
                                            String modified =
                                                    DateTimeFormatter.ofPattern("MMM dd HH:mm")
                                                            .withZone(ZoneId.systemDefault())
                                                            .format(
                                                                    attrs.lastModifiedTime()
                                                                            .toInstant());
                                            return String.format(
                                                    "%s %10d %s %s",
                                                    type, size, modified, path.getFileName());
                                        } catch (IOException e) {
                                            return path.getFileName()
                                                    + " (error reading attributes)";
                                        }
                                    })
                            .collect(Collectors.joining("\n"));
            return success(details);
        } catch (IOException e) {
            return error("Could not list directory: " + e.getMessage());
        }
    }

    private SuccessOrError<String> createDirectoryHelper(String pathString) {
        try {
            Path path = Files.createDirectories(Paths.get(pathString));
            return success("Successfully created directory: " + path);
        } catch (IOException e) {
            return error("Could not create directory: " + e.getMessage());
        }
    }

    private SuccessOrError<String> grepFileHelper(String pathString, String pattern, int context) {
        try {
            Path path = Paths.get(pathString);
            List<String> lines = Files.readAllLines(path);
            List<String> results = new ArrayList<>();
            Pattern p = Pattern.compile(pattern);

            for (int i = 0; i < lines.size(); i++) {
                if (p.matcher(lines.get(i)).find()) {
                    results.add("---");
                    int start = Math.max(0, i - context);
                    int end = Math.min(lines.size(), i + context + 1);
                    for (int j = start; j < end; j++) {
                        results.add(String.format("%d: %s", j + 1, lines.get(j)));
                    }
                }
            }
            return success(results.isEmpty() ? "No matches found." : String.join("\n", results));
        } catch (IOException e) {
            return error("Error reading or searching file: " + e.getMessage());
        }
    }

    private static int sortPaths(Path a, Path b) {
        boolean isDirectoryA = Files.isDirectory(a);
        boolean isDirectoryB = Files.isDirectory(b);

        if (isDirectoryA == isDirectoryB) {
            return a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString());
        } else {
            return isDirectoryA ? -1 : 1;
        }
    }

    /** Normalize line endings to make replacements more consistent */
    private String normalizeLineEndings(String text) {
        String normalized = text.replace("\r\n", "\n");
        return normalized.replace("\r", "\n");
    }

    /** Generate a git-style diff between original and modified content */
    private String generateDiff(String filePath, String originalContent, String modifiedContent) {
        originalContent = normalizeLineEndings(originalContent);
        modifiedContent = normalizeLineEndings(modifiedContent);

        StringBuilder diff = new StringBuilder();
        diff.append("--- ").append(filePath).append("\t(original)\n");
        diff.append("+++ ").append(filePath).append("\t(modified)\n");

        String[] originalLines = originalContent.split("\n");
        String[] modifiedLines = modifiedContent.split("\n");

        if (!originalContent.equals(modifiedContent)) {
            diff.append("@@ -1,")
                    .append(originalLines.length)
                    .append(" +1,")
                    .append(modifiedLines.length)
                    .append(" @@\n");
            for (String line : originalLines) {
                diff.append("-").append(line).append("\n");
            }
            for (String line : modifiedLines) {
                diff.append("+").append(line).append("\n");
            }
        } else {
            diff.append("No changes\n");
        }
        return diff.toString();
    }
}
