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
package dev.enola.core.docgen;

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.base.Charsets;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.EntityKindRepository;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MarkdownDocGeneratorTest {
    @Test
    public void testDocgen() throws IOException, ValidationException {
        EntityKindRepository repository = new EntityKindRepository();
        repository.load(new ClasspathResource("demo-model.textproto"));

        StringBuilder sb = new StringBuilder();
        new MarkdownDocGenerator().render(repository, sb);
        var got = sb.toString();

        var expected =
                new ClasspathResource("demo-model-docgen.md", Charsets.UTF_8).charSource().read();
        if (!got.equals(expected)) {
            // NOT System.getProperty("java.io.tmpdir")
            File tmp = new File("/tmp");
            var gotFile = new File(tmp, "MarkdownDocGeneratorTest-got.md");
            new FileResource(gotFile.toPath()).charSink().write(got);
            assertWithMessage(gotFile.toString()).that(got).isEqualTo(expected);
        }
    }
}
