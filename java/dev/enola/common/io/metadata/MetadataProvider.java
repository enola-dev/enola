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
package dev.enola.common.io.metadata;

import dev.enola.data.ProviderFromIRI;

public interface MetadataProvider extends ProviderFromIRI<Metadata> {

    @Override
    Metadata get(String iri);

    @Deprecated // TODO Replace with #get().label()
    String getLabel(String iri);

    @Deprecated // TODO Replace with #get().descriptionHTML()
    String getDescriptionHTML(String iri);

    @Deprecated // TODO Replace with #get().imageHTML()
    String getImageHTML(String iri);
}
