/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.name;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class NamedObjectProvidersTest {

    // TODO @Test public void newSingleThreaded() & newConcurrent()

    @Test
    public void newImmutable() {
        var nop = NamedObjectProviders.newImmutable(Map.of("foo", 1, "bar", 2L));
        assertThat(nop.get("foo", Integer.class, "Test")).isEqualTo(1);
        assertThat(nop.get("bar", Long.class, "Test")).isEqualTo(2L);
    }

    @Test
    public void implementationVsInterfaceClass() {
        var nop = NamedObjectProviders.newImmutable(Map.of("list", new ArrayList<String>()));
        assertThat(nop.get("list", List.class, "Test")).isInstanceOf(ArrayList.class);
        assertThat(nop.get("list", Collection.class, "Test")).isInstanceOf(ArrayList.class);
        assertThat(nop.get("list", Iterable.class, "Test")).isInstanceOf(ArrayList.class);
    }

    @Test
    public void implementationVsInterfaceClassConflict() {
        var nop =
                NamedObjectProviders.newSingleThreaded()
                        .store("list", new ArrayList<String>())
                        .store("list", new LinkedList<>());
        Assert.assertThrows(
                IllegalArgumentException.class, () -> nop.get("list", List.class, "Test"));
    }
}
