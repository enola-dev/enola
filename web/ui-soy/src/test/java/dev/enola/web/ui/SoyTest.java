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
package dev.enola.web.ui;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class SoyTest {

    // Test coverage for using Protobufs in https://github.com/google/closure-templates (AKA "Soy")
    // see https://github.com/google/closure-templates/issues/1300

    @Test
    public void testRender() throws ValidationException, IOException {
        var soy =
                new Soy.Builder()
                        .addSoy("dev/enola/core/docgen/markdown.soy")
                        .addProto(ID.getDescriptor())
                        .build();

        var ekr = new EntityKindRepository().load(new ClasspathResource("test-model.yaml"));
        Map<String, ?> params =
                ImmutableMap.of(
                        "package",
                        "test",
                        "kinds",
                        ekr.list().stream().map(EntityKind::getId).collect(Collectors.toSet()));
        var renderer = soy.newRenderer("dev.enola.markdown.package", params);

        var sb = new StringBuffer();
        renderer.renderText(sb).assertDone();
        var md = sb.toString();
        assertThat(md).contains("generated with ❤️ by");
    }

    @Test
    public void testProtoFileDescriptorName() {
        assertThat(ID.getDescriptor().getFile().getName())
                // This is the "path" used in `import {ID} from` in the *.soy file
                .isEqualTo("core/lib/src/main/java/dev/enola/core/enola_core.proto");
    }
}
