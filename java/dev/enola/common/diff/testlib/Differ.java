/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.diff.testlib;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRowGenerator;

class Differ {

    // https://github.com/java-diff-utils/java-diff-utils/wiki/Examples

    static String deltas(String original, String revised) {
        var originalLines = original.lines().toList();
        var revisedLines = revised.lines().toList();

        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);
        var sb = new StringBuilder();
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            sb.append(delta.getType());
            sb.append(' ');
            print(delta.getSource(), sb);
            sb.append('\n');
            sb.append("  =>   ");
            print(delta.getTarget(), sb);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void print(Chunk<String> chunk, StringBuilder sb) {
        sb.append(chunk);
    }

    static String markdown(String original, String revised) {
        var originalLines = original.lines().toList();
        var revisedLines = revised.lines().toList();

        DiffRowGenerator differ =
                DiffRowGenerator.create()
                        .showInlineDiffs(true)
                        .mergeOriginalRevised(true)
                        .inlineDiffByWord(true)
                        .oldTag(f -> "~") // introduce markdown style for strikethrough
                        .newTag(f -> "**") // introduce markdown style for bold
                        .build();
        var diffRows = differ.generateDiffRows(originalLines, revisedLines);
        var sb = new StringBuilder();
        for (var diffRow : diffRows) {
            sb.append(diffRow.getOldLine());
            sb.append('\n');
        }
        return sb.toString();
    }
}
