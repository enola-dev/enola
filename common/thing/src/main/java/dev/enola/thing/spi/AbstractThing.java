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
package dev.enola.thing.spi;

import com.google.common.base.MoreObjects;

import dev.enola.thing.Thing;

import java.util.Objects;

public abstract class AbstractThing implements Thing {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof Thing)) return false;
        final Thing other = (Thing) obj;
        return Objects.equals(this.iri(), other.iri())
                && Objects.equals(this.properties(), other.properties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(iri(), properties());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("iri", iri())
                .add("properties", properties())
                .toString();
    }
}
