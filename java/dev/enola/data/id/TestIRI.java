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
package dev.enola.data.id;

import com.google.errorprone.annotations.Immutable;

import dev.enola.data.id.IDIRI.ConverterX;
import dev.enola.data.iri.IRIConverter;

@Immutable
public class TestIRI extends IDIRI<TestID> {

    static final ConverterX<TestIRI, TestID> CONVERTER =
            new ConverterX<>("https://example.org/thing/", TestID.CONVERTER) {
                @Override
                protected TestIRI create(TestID id) {
                    return new TestIRI(id);
                }
            };

    public TestIRI(TestID testID) {
        super(testID);
    }

    @Override
    protected IRIConverter<TestIRI> iriConverter() {
        return CONVERTER;
    }

    @Override
    protected boolean isComparableTo(Object other) {
        return other instanceof TestIRI;
    }
}
