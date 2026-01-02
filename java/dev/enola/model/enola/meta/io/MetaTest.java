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
package dev.enola.model.enola.meta.io;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.yamljson.testlib.TestYaml;
import dev.enola.model.enola.meta.Schema;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingProvider;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class MetaTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    private final ThingMemoryRepositoryRW thingMemoryRepository = new ThingMemoryRepositoryRW();

    public @Rule TestTLCRule rule =
            new TestTLCRule(
                    ImmutableMap.of(
                            ThingProvider.class,
                            thingMemoryRepository,
                            ThingRepositoryStore.class,
                            thingMemoryRepository));

    private Schema read(String name) throws IOException {
        return new SchemaIO().readYAML(new ClasspathResource(name + ".yaml"));
    }

    private Schema expected(String name) throws IOException {
        var schema = read(name);
        TestYaml.assertEqualsToResource(schema, name + ".expected.yaml");
        return schema;
    }

    @Test
    public void readCommonYAML() throws IOException {
        read("enola.dev/common");
    }

    @Test
    public void readMetaSchemaYAML() throws IOException {
        read("enola.dev/meta");
    }

    @Test
    @Ignore // TODO Make this work (again)... it doesn't work anymore since switching to ProxyTBL
    public void testSchemaYAML() throws IOException {
        var test = expected("test.esch");
        assertThat(test.name()).isEqualTo("Test");
        // NB: We're not asserting on every attribute, because test.esch.expected.yaml did!
    }
}
