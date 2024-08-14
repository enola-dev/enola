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
package dev.enola.rdf;

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.StringResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.message.ThingAdapter;

import org.junit.Test;

/** Unit Tests with non-regression coverage with specific bugs. */
public class BugsTest {

    @Test
    public void npeRdfListProtoPredicatesObjectsAdapter() {
        DatatypeRepository dtr = DatatypeRepository.EMPTY;
        ResourceProvider rp = iri -> null;

        var rrc = new RdfReaderConverter(rp);
        var rdc = new RdfThingConverter();

        var rdf = "@prefix : <http://example.org>. :thing :property ( :thing1 ).";
        var resource = StringResource.of(rdf, RdfMediaTypes.TURTLE);
        var model = rrc.convert(resource).get();
        var protoThing = rdc.convert(model).findFirst().get().build();
        var thing = new ThingAdapter(protoThing, dtr);

        // Should not throw an NPE
        thing.properties();
    }
}
