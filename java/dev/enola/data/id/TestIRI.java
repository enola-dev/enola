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
package dev.enola.data.id;

import com.google.errorprone.annotations.Immutable;

import dev.enola.common.convert.ConversionException;
import dev.enola.data.iri.IRIConverter;

import java.io.IOException;
import java.util.Optional;

@Immutable
public class TestIRI extends IDIRI<TestID> {

    private static final String PREFIX = "https://example.org/thing/";

    public static final IRIConverter<TestIRI> CONVERTER =
            // TODO Create better abstraction for this... PrefixingIRIConverter?
            new IRIConverter<>() {
                @Override
                public Optional<TestIRI> convert(String input) throws ConversionException {
                    if (!input.startsWith(PREFIX)) return Optional.empty();
                    var opt = TestID.CONVERTER.convert(input.substring(PREFIX.length()));
                    var testID =
                            opt.orElseThrow(
                                    () -> new ConversionException("Not a TestID: " + input));
                    return Optional.of(new TestIRI(testID));
                }

                @Override
                public boolean convertInto(TestIRI from, Appendable into)
                        throws ConversionException, IOException {
                    into.append(PREFIX);
                    return TestID.CONVERTER.convertInto(from.id(), into);
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
