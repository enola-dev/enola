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
package dev.enola.thing.testlib;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.collect.Streams;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.testlib.ResourceSubject;
import dev.enola.rdf.io.JavaThingRdfConverter;
import dev.enola.rdf.io.RdfReaderConverter;
import dev.enola.rdf.io.RdfWriterConverter;
import dev.enola.thing.repo.ThingRepository;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.util.Models;

import java.io.IOException;

public final class ThingsSubject extends Subject {

    // TODO add assertThat(Thing actual) - with a SingleThingRepository ?

    public static ThingsSubject assertThat(ThingRepository actual) {
        return assertAbout(resources()).that(actual);
    }

    public static Factory<ThingsSubject, ThingRepository> resources() {
        return ThingsSubject::new;
    }

    private static final Model EMPTY_MODEL = new DynamicModel(new LinkedHashModelFactory());

    private final Model actualModel;
    private final ResourceProvider rp = new ClasspathResource.Provider();
    private final RdfReaderConverter rdfReaderConverter = new RdfReaderConverter(rp);
    private final RdfWriterConverter rdfWriterConverter = new RdfWriterConverter();

    public ThingsSubject(FailureMetadata metadata, ThingRepository actual) {
        super(metadata, actual);
        JavaThingRdfConverter javaThingRdfConverter = new JavaThingRdfConverter();
        actualModel = javaThingRdfConverter.convert(Streams.stream(actual.list()));
    }

    public void isEqualTo(String classpathResourcePath) throws IOException {
        var resource = rp.getReadableResource(classpathResourcePath);
        if (resource == null) throw new IllegalArgumentException(classpathResourcePath);
        var expectedModel = rdfReaderConverter.convert(resource).orElse(EMPTY_MODEL);
        if (!Models.isomorphic(actualModel, expectedModel)) {
            var actualResource = new MemoryResource(resource.mediaType());
            rdfWriterConverter.convertInto(actualModel, actualResource);
            ResourceSubject.assertThat(actualResource).containsCharsOf(resource);
        }
    }
}
