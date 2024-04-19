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
package dev.enola.zimpl;

import com.google.common.collect.ImmutableList;

import dev.enola.Action;
import dev.enola.Enola;
import dev.enola.common.convert.IdentityObjectWithTypeConverter;
import dev.enola.common.convert.ObjectWithTypeTokenConverter;
import dev.enola.common.convert.ObjectWithTypeTokenConverterChain;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.data.ProviderFromIRI;
import dev.enola.data.Repository;
import dev.enola.data.RepositoryBuilder;
import dev.enola.model.enola.Get;

public class EnolaProvider {

    ProviderFromIRI<?> objectProvider = new EmptyResource.Provider();

    ObjectWithTypeTokenConverter converter =
            new ObjectWithTypeTokenConverterChain(
                    ImmutableList.<ObjectWithTypeTokenConverter>builder()
                            .add(new IdentityObjectWithTypeConverter())
                            .build());

    Repository<Action<?, ?>> actionsRepo =
            new ActionRepositoryBuilder().store(new Get(objectProvider)).build();

    public Enola get() {
        return new EnolaImplementation(objectProvider, actionsRepo, converter);
    }

    private static class ActionRepositoryBuilder
            extends RepositoryBuilder<ActionRepositoryBuilder, Action<?, ?>> {

        @Override
        protected String getIRI(Action<?, ?> action) {
            return action.iri();
        }
    }
}
