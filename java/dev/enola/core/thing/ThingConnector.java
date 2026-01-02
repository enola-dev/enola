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
package dev.enola.core.thing;

import com.google.protobuf.Descriptors;

import dev.enola.common.convert.ConversionException;
import dev.enola.core.EnolaException;
import dev.enola.thing.proto.Things;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * API for in-process Thing "connectors".
 *
 * <p>This is the internal equivalent of the gRPC ConnectorService.
 */
// TODO Is this still needed? @SuppressWarnings("restriction")
public interface ThingConnector {
    // TODO Move to lib?

    // TODO Type type();
    String iri();

    void augment(Things.Builder thingsBuilder, String iri, Map<String, String> parameters)
            throws UncheckedIOException, ConversionException;

    default List<Descriptors.Descriptor> getDescriptors() throws EnolaException {
        return Collections.emptyList();
    }
}
