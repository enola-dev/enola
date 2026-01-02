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
package dev.enola.thing.message;

import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;

import org.jspecify.annotations.Nullable;

public class ProtoThingMetadataProvider implements MetadataProvider<Thing> {

    private final ThingMetadataProvider thingMetadataProvider;

    public ProtoThingMetadataProvider(ThingMetadataProvider thingMetadataProvider) {
        this.thingMetadataProvider = thingMetadataProvider;
    }

    @Override
    public Metadata get(String iri) {
        return thingMetadataProvider.get(iri);
    }

    @Override
    public Metadata get(Thing thing) {
        return thingMetadataProvider.get(new ThingAdapter(thing));
    }

    @Override
    public Metadata get(@Nullable Thing thing, String iri) {
        if (thing != null) return thingMetadataProvider.get(new ThingAdapter(thing), iri);
        else return thingMetadataProvider.get(null, iri);
    }
}
