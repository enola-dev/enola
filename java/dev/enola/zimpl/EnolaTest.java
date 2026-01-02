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
package dev.enola.zimpl;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.Enola;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.model.enola.action.Actions;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class EnolaTest {

    // TODO Replace this with //test/test.enola scripting...

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    Enola e = new EnolaProvider().get();

    @Test
    public void emptyGet() {
        var r = e.act(EmptyResource.EMPTY_URI, Actions.GET);
        assertThat(r).isInstanceOf(EmptyResource.class);
        // TODO Test convert, assert empty String
    }

    @Test
    public void stringGet() {
        var r = e.act(DataResource.of("hello, world").uri(), Actions.GET);
        assertThat(r).isInstanceOf(DataResource.class);
        // TODO Test convert to String, assert "hello, world"
    }

    @Test
    @Ignore // TODO FIXME
    public void getList() {
        // TODO Constant for "enola:/" from where? It's also a Model...
        var r = e.act("enola:/", Actions.GET);
        assertThat(r).isInstanceOf(Iterable.class);
        // TODO Assert contains EmptyResource.EMPTY_URI and StringResource.TEMPLATE
    }
}
