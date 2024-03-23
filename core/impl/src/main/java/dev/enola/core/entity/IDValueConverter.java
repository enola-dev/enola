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
package dev.enola.core.entity;

import dev.enola.common.convert.ConversionException;
import dev.enola.core.IDs;
import dev.enola.core.proto.ID;
import dev.enola.thing.message.ObjectToValueConverter;
import dev.enola.thing.proto.Value;

import java.util.Optional;

public class IDValueConverter implements ObjectToValueConverter {

    @Override
    public Optional<Value.Builder> convert(Object input) throws ConversionException {
        if (!(input instanceof ID)) return Optional.empty();
        ID id = (ID) input;
        var path = IDs.toPath(id);
        var link = Value.Link.newBuilder().setIri("enola:" + path);
        return Optional.of(Value.newBuilder().setLink(link));
    }
}
