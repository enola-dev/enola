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
package dev.enola.rdf.io;

import com.google.protobuf.Message;

import dev.enola.common.convert.OptionalConverter;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import java.util.List;

// TODO Move this interface to another package, as it's not actually RDF specific
public interface ResourceIntoProtoThingConverter
        extends OptionalConverter<ReadableResource, List<Thing.Builder>> {

    /** This returns thingsList as Thing (if there is 1) or a {@link Things} pb. */
    default Message.Builder asMessage(List<Thing.Builder> thingsList) {
        Message.Builder messageBuilder;
        if (thingsList.size() == 1) messageBuilder = thingsList.get(0);
        else {
            var thingsBuilder = Things.newBuilder();
            for (var thing : thingsList) {
                thingsBuilder.addThings(thing);
            }
            messageBuilder = thingsBuilder;
        }
        return messageBuilder;
    }
}
