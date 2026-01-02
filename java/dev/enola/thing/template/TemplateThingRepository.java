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
package dev.enola.thing.template;

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import dev.enola.common.collect.MoreIterables;
import dev.enola.data.iri.template.URITemplateMatcherChain;
import dev.enola.data.iri.template.VariableMaps;
import dev.enola.thing.*;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.repo.ThingRepository;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;

public class TemplateThingRepository implements ThingRepository, TemplateService {

    private record Match(String iriTemplate, Function<Map<String, String>, Thing> function) {}

    private final ThingRepository delegate;
    private final URITemplateMatcherChain<Match> iriTemplateChain;

    public TemplateThingRepository(ThingRepository delegate) {
        this.delegate = delegate;
        int size = MoreIterables.sizeIfKnown(delegate.list()).orElse(42);
        var iriTemplateChainBuilder = URITemplateMatcherChain.<Match>builderWithExpectedSize(size);
        for (var thing : delegate.list()) {
            if (!thing.isIterable(KIRI.RDF.TYPE)
                    && KIRI.RDFS.CLASS.equals(thing.getString(KIRI.RDF.TYPE))) {
                thing.getOptional(KIRI.E.IRI_TEMPLATE_PROPERTY, String.class)
                        .ifPresent(
                                iriTemplate ->
                                        iriTemplateChainBuilder.add(
                                                iriTemplate, gen(iriTemplate, thing)));
            }
        }
        this.iriTemplateChain = iriTemplateChainBuilder.build();
    }

    private Match gen(String classIRITemplate, Thing rdfClass) {
        Set<SimpleImmutableEntry<String, URITemplate>> set = new HashSet<>();
        for (String predicateIRI : rdfClass.predicateIRIs()) {
            if (KIRI.E.IRI_TEMPLATE_DATATYPE.equals(rdfClass.datatype(predicateIRI))) {
                var uriTemplate = newURITemplate(rdfClass.getString(predicateIRI));
                set.add(new SimpleImmutableEntry<String, URITemplate>(predicateIRI, uriTemplate));
            }
        }
        var templatePredicates = Collections.unmodifiableSet(set);

        var classURITemplate = newURITemplate(classIRITemplate);
        return new Match(
                classIRITemplate,
                params -> {
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
                });
    }

    private URITemplate newURITemplate(String template) {
        try {
            return new URITemplate(template);
        } catch (URITemplateParseException e) {
            throw new IllegalArgumentException(template + " invalid IRI Template", e);
        }
    }

    @Override
    public Iterable<String> listIRI() {
        return Iterables.concat(iriTemplateChain.listTemplates(), delegate.listIRI());
    }

    @Override
    public @Nullable Thing get(String iri) {
        // TODO Check delegate first, and merge() if found... with TDD!
        var optEntry = iriTemplateChain.match(iri);
        if (optEntry.isPresent()) {
            var entry = optEntry.get();
            var match = entry.getKey();
            var params = entry.getValue();
            var function = match.function;
            return function.apply(params);
        } else return delegate.get(iri);
    }

    @Override
    public Optional<Breakdown> breakdown(String nonTemplateIRI) {
        if (Templates.hasVariables(nonTemplateIRI))
            throw new IllegalArgumentException("Template: " + nonTemplateIRI);
        var optEntry = iriTemplateChain.match(nonTemplateIRI);
        if (optEntry.isPresent()) {
            var entry = optEntry.get();
            var match = entry.getKey();
            var params = entry.getValue();
            var iriTemplate = match.iriTemplate;
            return Optional.of(new Breakdown(iriTemplate, params));
        } else return Optional.empty();
    }
}
