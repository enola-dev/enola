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
package dev.enola.thing.validation;

import com.google.common.collect.ImmutableList;

import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingRepository;

public class Validators {

    private final ImmutableList<Validator<PredicatesObjects>> validators;

    Validators(Validator<PredicatesObjects>... validators) {
        this.validators = ImmutableList.copyOf(validators);
    }

    public Validators(ThingRepository repo) {
        this(new LinksValidator(repo));
    }

    public void validate(ThingRepository repository, Collector2 collector) {
        validate(repository.list(), collector);
    }

    public void validate(Iterable<Thing> things, Collector2 collector) {
        // TODO #performance Multi-threaded parallelized implementation?
        for (Thing thing : things) {
            validate(thing, collector);
        }
    }

    private void validate(Thing root, Collector2 collector) {
        // TODO #performance Multi-threaded parallelized implementation?
        for (var validator : validators) {
            validator.validate(root, new InternalCollector(root, collector));
        }
    }

    private static class InternalCollector implements Collector {

        private final Thing thing;
        private final Collector2 collector2;

        private InternalCollector(Thing thing, Collector2 collector2) {
            this.collector2 = collector2;
            this.thing = thing;
        }

        @Override
        public void add(String predicateIRI, String message) {
            collector2.add(thing, predicateIRI, message);
        }
    }
}
