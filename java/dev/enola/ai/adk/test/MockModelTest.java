/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.ai.adk.test;

import org.junit.Test;

public class MockModelTest {

    @Test
    public void mock() {
        var model = new MockModel("bar");
        var modelTester = new ModelTester(model);

        modelTester.assertTextResponseEquals("foo", "bar");
        // TODO assertThat(model).prompt("foo").responseContains("bar");

        // Prompt it again, and it should reply with "bar" again (not with nothing, or error)
        modelTester.assertTextResponseEquals("foo", "bar");
    }
}
