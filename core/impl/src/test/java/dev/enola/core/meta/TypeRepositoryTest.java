/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.core.meta;

import dev.enola.common.io.resource.FileResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.meta.proto.Types;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class TypeRepositoryTest {
    @Test
    public void loadBaseYAML() throws IOException {
        var types = Types.newBuilder();
        var resource =
                // TODO Fix absolute path... use ClasspathResource - needs BUILD in
                // docs/models/enola?
                new FileResource(
                        Path.of(
                                "/home/vorburger/git/github.com/enola-dev/enola/docs/models/enola/enola.types.yaml"));
        new ProtoIO().read(resource, types, Types.class);
    }
}
