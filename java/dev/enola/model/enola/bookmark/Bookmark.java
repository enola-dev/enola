/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.model.enola.bookmark;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.model.enola.HasDescription;
import dev.enola.model.enola.Tag;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.thing.Thing;
import dev.enola.thing.java.HasType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

public interface Bookmark extends HasType, HasLabel, HasDescription {

    // TODO Are default implementations of these interface methods still require now?!

    // TODO WebPage? URI?
    String url();

    List<Tag> tags();

    // TODO Subject instead of void
    void by(); // owner

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends Bookmark> // skipcq: JAVA-E0169
            extends HasType.Builder<B>,
                    HasLabel.Builder<B>,
                    HasDescription.Builder<B>,
                    Thing.Builder<B> {

        @Override
        @CanIgnoreReturnValue
        Builder<B> iri(String iri);

        Builder<B> url(String url);

        Builder<B> tags(List<Tag> tags);

        Builder<B> by(String owner);
    }
}
