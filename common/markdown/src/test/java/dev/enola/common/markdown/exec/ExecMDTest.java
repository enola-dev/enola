/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.common.markdown.exec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class ExecMDTest {

    private static final Path CWD = Path.of(".");

    @Test
    public void testEcho() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash\n$ echo hi\nIGNORED\n```");
        assertEquals("# Test\n```bash\n$ echo hi\nhi\n```\n", r.markdown);
        assertEquals("echo hi\n\n\nsleep ${SLEEP:-7}\n", r.script);
    }

    @Test
    public void testFalse() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash $?\n$ false\n```");
        assertEquals("# Test\n```bash\n$ false\n```\n", r.markdown);
        assertEquals("false\n\n\nsleep ${SLEEP:-7}\n", r.script);
    }

    @Test
    public void testPreambleInit() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash MSG=hi\n$ echo $MSG\n```");
        assertEquals("# Test\n```bash\n$ echo $MSG\nhi\n```\n", r.markdown);
        assertEquals(":MSG=hi\necho $MSG\n\n\nsleep ${SLEEP:-7}\n", r.script);
    }

    @Test
    public void testMultiline() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "```bash\n$ echo \\\n  Hi\n```");
        assertEquals("```bash\n$ echo \\\n  Hi\nHi\n```\n", r.markdown);
        assertEquals("echo \\\n  Hi\n\n\nsleep ${SLEEP:-7}\n", r.script);
    }
}
