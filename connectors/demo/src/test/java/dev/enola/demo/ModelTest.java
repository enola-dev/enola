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
package dev.enola.demo;

import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.meta.proto.MetaModel;
import dev.enola.core.proto.Entity;

import org.junit.Test;

import java.io.IOException;

public class ModelTest {

    @Test
    public void testTextprotos() throws IOException {
        ProtoIO.check("dev/enola/demo/demo-model.textproto", MetaModel.newBuilder());
        ProtoIO.check("dev/enola/demo/foo-abc.textproto", Entity.newBuilder());
        ProtoIO.check("dev/enola/demo/bar-abc-def.textproto", Entity.newBuilder());
    }
}
