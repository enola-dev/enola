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
package dev.enola.thing.template;

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import dev.enola.common.MoreIterables;
import dev.enola.common.io.iri.template.URITemplateMatcherChain;
import dev.enola.common.io.iri.template.VariableMaps;
import dev.enola.thing.*;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;

public class TemplateThingRepository implements ThingRepository {

    private final ThingRepository delegate;
    private final URITemplateMatcherChain<Function<Map<String, String>, Thing>> iriTemplateChain;

    public TemplateThingRepository(ThingRepository delegate) {
        this.delegate = delegate;
        int size = MoreIterables.sizeIfKnown(delegate.list()).orElse(42);
        var iriTemplateChainBuilder =
                URITemplateMatcherChain
                        .<Function<Map<String, String>, Thing>>builderWithExpectedSize(size);
        for (var thing : delegate.list()) {
            if (KIRI.RDFS.CLASS.equals(thing.getString(KIRI.RDF.TYPE))) {
                thing.getOptional(KIRI.E.IRI_TEMPLATE_PROPERTY, String.class)
                        .ifPresent(
                                iriTemplate ->
                                        iriTemplateChainBuilder.add(
                                                iriTemplate, gen(iriTemplate, thing)));
            }
        }
        this.iriTemplateChain = iriTemplateChainBuilder.build();
    }

    private Function<Map<String, String>, Thing> gen(String classIRITemplate, Thing rdfClass) {
        try {
            Set<SimpleImmutableEntry<String, URITemplate>> set = new HashSet<>();
            for (String predicateIRI : rdfClass.predicateIRIs()) {
                if (KIRI.E.IRI_TEMPLATE_DATATYPE.equals(rdfClass.datatype(predicateIRI))) {
                    var uriTemplate = new URITemplate(rdfClass.getString(predicateIRI));
                    set.add(
                            new SimpleImmutableEntry<String, URITemplate>(
                                    predicateIRI, uriTemplate));
                }
            }
            var templatePredicates = Collections.unmodifiableSet(set);

            var classURITemplate = new URITemplate(classIRITemplate);
            return params -> {
                try {
                    var varMap = VariableMaps.from(params);
                    var builder = ImmutableThing.builder();
                    var newIRI = Templates.unescapeURL(classURITemplate.toString(varMap));
                    builder.iri(newIRI);
                    builder.set(KIRI.RDF.TYPE, new Link(rdfClass.iri()));
                    for (var templatePredicate : templatePredicates) {
                        var predicateIRI = templatePredicate.getKey();
                        var predicateURITemplate = templatePredicate.getValue();
                        var link = Templates.unescapeURL(predicateURITemplate.toString(varMap));
                        builder.set(predicateIRI, new Link(link));
                    }

                    return builder.build();
                } catch (URITemplateException e) {
                    throw new IllegalArgumentException(rdfClass.iri(), e);
                }
            };
        } catch (URITemplateParseException e) {
            throw new IllegalArgumentException(rdfClass.iri() + " invalid IRI Template", e);
        }
    }

    @Override
    public @Nullable Thing get(String iri) {
        // TODO Check delegate first, and merge() if found... with TDD!
        var match = iriTemplateChain.match(iri);
        if (match.isPresent()) {
            var entry = match.get();
            var params = entry.getValue();
            var function = entry.getKey();
            return function.apply(params);
        } else return delegate.get(iri);
    }

    @Override
    public Iterable<String> listIRI() {
        return Iterables.concat(iriTemplateChain.listTemplates(), delegate.listIRI());
    }
}
