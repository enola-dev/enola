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
package dev.enola.common.io.object.template;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.TestResource;

import java.io.IOException;
import java.nio.file.Paths;

// This can be used e.g. as:
//   b run //java/dev/enola/common/io/object/template:temply -- $PWD/bom.hbs.yaml
public class TemplyMain {

    // TODO Remove; as replaced by TemplateResourceConverter & Co?

    // TODO Expose this as an ./enola template CLI sub-command too

    public static void main(String[] args) throws ConversionException, IOException {
        if (args.length != 1) {
            System.err.println("USAGE: TemplyMain <XYZ.hbs.yaml>");
            System.exit(1);
        }
        var templateFile = args[0];

        MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes());

        var fileRP = new FileResource.Provider();
        var fdRP = new FileDescriptorResource.Provider();
        var dataRP = new DataResource.Provider();
        var testRP = new TestResource.Provider();
        var rp = new ResourceProviders(fileRP, fdRP, dataRP, testRP);

        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            var in = rp.getReadableResource(templateFile);
            var out =
                    rp.getNonNull(
                            URIs.addMediaType(
                                    FileDescriptorResource.STDOUT_URI, YamlMediaType.YAML_UTF_8));

            var temply = new Temply();
            temply.convertIntoOrThrow(in, out);
        }
    }
}
