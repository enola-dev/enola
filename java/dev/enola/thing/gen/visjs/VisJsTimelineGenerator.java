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
package dev.enola.thing.gen.visjs;

import com.google.common.collect.Iterables;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ReplacingResource;
import dev.enola.common.time.Interval;
import dev.enola.common.yamljson.JSON;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.LinkTransformer;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.metadata.ThingTimeProvider;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class VisJsTimelineGenerator implements ThingsIntoAppendableConverter {

    // TODO "Define a start and an end in the timeline options. This will improve initial loading."

    // TODO https://github.com/javdome/timeline-arrows

    // TODO #feature Support Timezone, but don't convert in Java, but purely client side in HTML

    // TODO Use numeric (long) group & item id: instead of String IRI

    // TODO HTML Fragment composition - "emit" to x3 "slots" (?) for HTML, JS & CSS...
    // Perhaps inspired by e.g. https://nbformat.readthedocs.io/en/latest/format_description.html ?
    // It should separate "fixed" (static) JS libs, from the "dynamic" data dependant output.
    // With a class HTML, maybe?

    // TODO Nested SubGroups, using ThingHierarchyProvider - instead of only by Thing rdf:type
    // TODO Load groups & items as JSON from URL, instead of embedding in page
    // TODO Add URL parameter to add "configure: true" to var options = { }

    // TODO #optimize Don't use ReplacingResource, but simply split Template?

    private final ThingTimeProvider timeProvider = new ThingTimeProvider();
    private final ThingMetadataProvider metadataProvider;
    private final LinkTransformer linkTransformer;

    private static final ReadableResource template =
            new ClasspathResource("dev/enola/thing/gen/visjs/timeline-template.html");

    public VisJsTimelineGenerator(
            ThingMetadataProvider metadataProvider, LinkTransformer linkTransformer) {
        this.metadataProvider = metadataProvider;
        this.linkTransformer = linkTransformer;
    }

    @Override
    public boolean convertInto(Iterable<Thing> things, Appendable into)
            throws ConversionException, IOException {

        var groupsAndItemsJSON = groupsAndItemsJSON(things);
        new ReplacingResource(
                        template, "// GROUPS_AND_ITEMS", groupsAndItemsText(groupsAndItemsJSON))
                .charSource()
                .copyTo(into);
        return true;
    }

    String groupsAndItemsText(GroupsAndItems groupsAndItemsJSON) {
        return "var groups = "
                + JSON.write(groupsAndItemsJSON.groups, true)
                + "\n      var items = "
                + JSON.write(groupsAndItemsJSON.items, true);
    }

    GroupsAndItems groupsAndItemsJSON(Iterable<Thing> things) {
        var groups = new HashMap<String, Group>();
        var items = new ArrayList<Item>();
        for (var thing : things) {
            String url = linkTransformer.get(thing.iri());
            var thingLabel = label(metadataProvider.get(thing));

            // TODO How to best handle Things with multiple parent types...
            if (thing.isIterable(KIRI.RDF.TYPE)) continue;

            var NO_TYPE = "NO-TYPE";
            var type = thing.getOptional(KIRI.RDF.TYPE, String.class).orElse(NO_TYPE);
            var typeLabel = !type.equals("NO-TYPE") ? label(metadataProvider.get(type)) : NO_TYPE;

            var intervals = timeProvider.existance(thing);
            // TODO Factor this out - but how & into where?
            var intervalsIterator = intervals.iterator();
            if (intervalsIterator.hasNext() && intervalsIterator.next().equals(Interval.ALL))
                continue;

            if (!Iterables.isEmpty(intervals)) {
                groups.put(type, new Group(type, typeLabel));
            }

            for (var interval : intervals) {
                var start = interval.isUnboundedStart() ? null : interval.start();
                var end = interval.isUnboundedEnd() ? null : interval.end();
                // NB: Vis.js needs either start & end, or just start - but never only end
                // TODO Isn't this missing handling of the other cases?
                if (start == null && end != null) {
                    start = end;
                    end = null;
                }
                items.add(new Item(thing.iri(), type, thingLabel, url, start, end));
            }
        }
        return new GroupsAndItems(groups.values(), items);
    }

    private record Group(String id, String content) {}

    private record Item(
            String id,
            String group,
            String label,
            @Nullable String url,
            @Nullable Instant start,
            @Nullable Instant end) {}

    private record GroupsAndItems(Collection<Group> groups, List<Item> items) {}
}
