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
package dev.enola.rdf.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.thing.proto.Thing;

import java.util.List;
import java.util.Optional;

// TODO Move this interface to another package, as it's not actually RDF specific
public class FilteringResourceIntoProtoThingConverter implements ResourceIntoProtoThingConverter {

    // TODO This is an ugly temporary hack, to unblock https://github.com/enola-dev/enola/pull/1781.
    // The better solution might be to use a proper cache? Or (or also) make RdfReaderConverterInto
    // ignore HTML instead of RDF? It should already do that, but it clearly didn't work (well),
    // yet. Without this, the "CI=1 ./test.bash" fails.

    private final ResourceIntoProtoThingConverter delegate;
    private final List<String> uriPrefixesToSkip;

    public FilteringResourceIntoProtoThingConverter(ResourceIntoProtoThingConverter delegate) {
        this.delegate = delegate;
        this.uriPrefixesToSkip =
                List.of(
                        "https://example.org",
                        "http://example.org",
                        "http://www.w3.org",
                        "https://schema.org",
                        "https://enola.dev",
                        "https://docs.enola.dev");
    }

    @Override
    public Optional<List<Thing.Builder>> convert(ReadableResource input)
            throws ConversionException {
        var url = input.uri().toString();
        if (uriPrefixesToSkip.stream().noneMatch(url::startsWith)) return delegate.convert(input);
        else return Optional.empty();
    }
}
