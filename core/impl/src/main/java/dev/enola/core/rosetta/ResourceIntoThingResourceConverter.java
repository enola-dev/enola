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
package dev.enola.core.rosetta;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.thing.ThingMediaTypes;

public class ResourceIntoThingResourceConverter implements CatchingResourceConverter {

    private final ThingMediaTypes thingMediaTypes = new ThingMediaTypes();
    private final ResourceIntoThingConverter ritc = new ResourceIntoThingConverter();
    private final ProtoIO protoIO = new ProtoIO();

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        var intoMediaType = thingMediaTypes.detect(into);
        if (intoMediaType.isPresent()) {
            var optThingsList = ritc.convert(from);
            if (optThingsList.isEmpty()) return false;

            // if (ThingMediaTypes.THING_HTML_UTF_8.equals(intoMediaType.get())) {
            // TODO Convert Thing to HTML fragment! (Test if mkdocs allows embedding HTML?)

            // } else {
            var thingsList = optThingsList.get();
            var message = ritc.asMessage(thingsList).build();
            protoIO.write(message, into);
            // }
            return true;
        } else {
            return false;
        }
    }
}
