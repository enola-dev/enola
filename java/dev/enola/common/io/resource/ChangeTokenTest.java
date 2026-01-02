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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class ChangeTokenTest {

    @Test
    public void emptyResource() {
        var resource = EmptyResource.INSTANCE;
        var changeToken = resource.changeToken();
        var changeTokenAsString = changeToken.toString();

        assertThat(changeTokenAsString).isNotEmpty();
        assertThat(changeToken.isDifferent(changeToken)).isFalse();
        assertThat(resource.isDifferent(changeTokenAsString)).isFalse();

        var changeToken2 = resource.changeToken();
        assertThat(changeToken.isDifferent(changeToken2)).isFalse();
    }

    @Test
    public void dataResource1() {
        var resource = DataResource.of("hello, world");
        var changeToken = resource.changeToken();
        var changeTokenAsString = changeToken.toString();

        assertThat(changeTokenAsString).isNotEmpty();
        assertThat(changeToken.isDifferent(changeToken)).isFalse();

        var changeToken2 = resource.changeToken();
        assertThat(changeToken.isDifferent(changeToken2)).isFalse();

        assertThat(resource.isDifferent(changeTokenAsString)).isFalse();
    }

    @Test
    public void dataResource2() {
        var resource1 = DataResource.of("hello, world");
        var changeToken1 = resource1.changeToken();
        var changeToken1AsString = changeToken1.toString();

        var resource2 = DataResource.of("hello, universe");
        var changeToken2 = resource2.changeToken();
        // TODO ? var changeToken2AsString = changeToken2.toString();

        assertThat(changeToken1.isDifferent(changeToken2)).isTrue();
        assertThat(resource2.isDifferent(changeToken1AsString)).isTrue();
    }

    @Test
    public void dataResourceBytes() {
        var resource1 = DataResource.of("hello, world");
        var changeToken1Bytes = resource1.changeToken().toBytes();
        assertThat(resource1.isDifferent(changeToken1Bytes)).isFalse();

        var resource2 = DataResource.of("hello, universe");
        assertThat(resource2.isDifferent(changeToken1Bytes)).isTrue();
    }

    @Test
    public void invalidMultibase() {
        assertThat(EmptyResource.INSTANCE.isDifferent("invalid-multibase")).isTrue();
    }
}
