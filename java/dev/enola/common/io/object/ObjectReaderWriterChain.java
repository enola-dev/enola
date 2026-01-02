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
package dev.enola.common.io.object;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import dev.enola.common.io.resource.WritableResource;

import java.io.IOException;

public class ObjectReaderWriterChain extends ObjectReaderChain implements ObjectReaderWriter {

    private final Iterable<ObjectReaderWriter> readerWriters;

    public ObjectReaderWriterChain(Iterable<ObjectReaderWriter> readerWriters) {
        super(map(readerWriters));
        this.readerWriters = readerWriters;
    }

    public ObjectReaderWriterChain(ObjectReaderWriter... readerWriters) {
        this(ImmutableList.copyOf(readerWriters));
    }

    private static Iterable<ObjectReader> map(Iterable<ObjectReaderWriter> readerWriters) {
        var readers =
                ImmutableList.<ObjectReader>builderWithExpectedSize(Iterables.size(readerWriters));
        readers.addAll(readerWriters);
        return readers.build();
    }

    @Override
    public boolean write(Object instance, WritableResource resource) throws IOException {
        for (var writer : readerWriters) {
            if (writer.write(instance, resource)) return true;
        }
        return false;
    }
}
