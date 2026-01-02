/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class ExecMDTest {

    private static final Path CWD = Path.of(".");

    @Test
    public void echo() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash\n$ echo hi\nIGNORED\n```");
        assertEquals("# Test\n```bash\n$ echo hi\nhi\n```\n", r.markdown);
        assertThat(r.script).contains("echo hi");
    }

    @Test
    public void isFalse() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash $?\n$ false\n```");
        assertEquals("# Test\n```bash\n$ false\n```\n", r.markdown);
        assertThat(r.script).contains("false");
    }

    @Test
    public void preambleInit() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "# Test\n```bash MSG=hi\n$ echo $MSG\n```");
        assertEquals("# Test\n```bash\n$ echo $MSG\nhi\n```\n", r.markdown);
        assertThat(r.script).contains(":MSG=hi\necho $MSG");
    }

    @Test
    public void multiline() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "```bash\n$ echo \\\n  Hi\n```");
        assertEquals("```bash\n$ echo \\\n  Hi\nHi\n```\n", r.markdown);
        assertThat(r.script).contains("echo \\\n  Hi");
    }

    @Test
    public void eof_eol() throws MarkdownProcessingException, IOException {
        var r = new ExecMD().process(CWD, "```bash\n$ echo -n \"hello, \" && echo -n world\n```");
        assertEquals(
                "```bash\n$ echo -n \"hello, \" && echo -n world\nhello, world\n```\n", r.markdown);
        assertThat(r.script).contains("echo -n \"hello, \" && echo -n world");
    }
}
