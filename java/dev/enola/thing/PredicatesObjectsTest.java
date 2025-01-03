/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.thing;

import static com.google.common.truth.Truth.assertThat;

import org.jspecify.annotations.Nullable;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PredicatesObjectsTest {

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    static class TestPredicatesObjects implements PredicatesObjects {

        private final String predicateIRI;
        private final Object object;

        TestPredicatesObjects(String predicateIRI, Object object) {
            this.predicateIRI = predicateIRI;
            this.object = object;
        }

        @Override
        public Map<String, Object> properties() {
            return Map.of(predicateIRI, object);
        }

        @Override
        public Set<String> predicateIRIs() {
            return Set.of(predicateIRI);
        }

        @Override
        public @Nullable String datatype(String predicateIRI) {
            return null;
        }

        @Override
        public Map<String, String> datatypes() {
            return Map.of();
        }

        @Override
        public <T> @Nullable T get(String predicateIRI) {
            if (predicateIRI.equals(this.predicateIRI)) return (T) object;
            else return null;
        }

        @Override
        public Builder<? extends PredicatesObjects> copy() {
            return null;
        }
    }

    List<Link> list = List.of(new Link("test:one"), new Link("test:two"));
    PredicatesObjects predicatesObjects = new TestPredicatesObjects("test:it", list);

    @Test
    public void x() {
        assertThat(predicatesObjects.getLinks("test:it")).isEqualTo(list);
    }
}
