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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.model.enola.meta.Common;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.net.URI;

// NB: This hand-written class may eventually get replaced by a code-generated one!
public abstract class MutableCommon extends ThrowingThing implements Common, Common.Builder {

    private String iri;
    private String name;
    private @Nullable String label;
    private @Nullable String description;
    private @Nullable String emoji;
    private @Nullable URI image;

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public Thing.Builder iri(String iri) {
        this.iri = iri;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Common.Builder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public MutableCommon label(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public MutableCommon description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String emoji() {
        return emoji;
    }

    @Override
    public MutableCommon emoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    @Override
    public URI image() {
        return image;
    }

    @Override
    public MutableCommon image(URI image) {
        this.image = image;
        return this;
    }
}
