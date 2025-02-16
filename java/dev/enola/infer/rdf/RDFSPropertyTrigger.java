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
package dev.enola.infer.rdf;

import dev.enola.model.w3.rdf.Property;
import dev.enola.thing.repo.ThingTrigger;

import org.jspecify.annotations.Nullable;

public class RDFSPropertyTrigger extends ThingTrigger<Property> {

    protected RDFSPropertyTrigger() {
        super(Property.class);
    }

    @Override
    public void updated(@Nullable Property existing, Property update) {

        // TODO implement remove existing...

        // TODO Avoid ifPresent() and do always
        update.domain()
                .ifPresent(
                        clazz -> {
                            if (!clazz.hasRdfsClassProperty(update.iri())) {
                                var builder = clazz.copy();
                                builder.addRdfsClassProperty(update);
                                repo.store(builder.build());
                            }
                        });
    }
}
