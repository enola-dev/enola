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
package dev.enola.model.enola.bookmarks;

import dev.enola.model.enola.HasDescription;
import dev.enola.model.w3.rdf.HasType;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.thing.Thing;

public interface Bookmark extends Thing, HasType, HasLabel, HasDescription {

    String url();

    String owner();

    interface Builder<B extends Bookmark>
            extends Thing.Builder<B>,
                    HasType.Builder<B>,
                    HasLabel.Builder<B>,
                    HasDescription.Builder<B> { // skipcq: JAVA-E0169

        Bookmark.Builder<B> url(String url);

        Bookmark.Builder<B> owner(String owner);

        Bookmark.Builder<B> label(String label);
    }
}
