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
package dev.enola.common.io.object.jackson;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Map;

class SkipEmptySequenceRepresenter extends Representer {

    // This class is currently unused (because the JacksonObjectReaderWriter does this);
    // but kept lingering around here, for now; in case there is another use for it later.

    private final Node EMPTY;

    public SkipEmptySequenceRepresenter(DumperOptions options) {
        super(options);
        EMPTY = representScalar(Tag.NULL, "");
    }

    @Override
    protected Node representSequence(
            Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle) {
        if (!sequence.iterator().hasNext()) return EMPTY;
        else return super.representSequence(tag, sequence, flowStyle);
    }

    @Override
    protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
        if (mapping.isEmpty()) return EMPTY;
        else return super.representMapping(tag, mapping, flowStyle);
    }
}
