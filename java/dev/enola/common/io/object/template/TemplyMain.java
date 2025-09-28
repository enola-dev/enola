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

import static dev.enola.common.io.resource.FileDescriptorResource.STDOUT_URI;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.TestResource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

// This can be used e.g. as:
//   b run //java/dev/enola/common/io/object/template:temply -- $PWD/bom.hbs.yaml
public class TemplyMain {

    // TODO Remove; as replaced by TemplateResourceConverter & Co?

    // TODO Expose this as an ./enola template CLI sub-command too

    private final ResourceProvider rp;

    public TemplyMain() {
        var fileRP = new FileResource.Provider();
        var fdRP = new FileDescriptorResource.Provider();
        var dataRP = new DataResource.Provider();
        var testRP = new TestResource.Provider();
        this.rp = new ResourceProviders(fileRP, fdRP, dataRP, testRP);
    }

    public static void main(String[] args) throws ConversionException, IOException {
        if (args.length < 1) {
            var use = "USAGE: [<data1/2.yaml|json>...] <config.hbs.yaml> | <template.handlebars>";
            System.out.println(use);
            System.exit(1);
        }
        var data = List.of(Arrays.copyOfRange(args, 0, args.length - 1));
        var dataURIs = data.stream().map(URI::create).toList();
        var templateURI = URI.create(args[args.length - 1]);

        MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes());
        var outURI = URIs.addMediaType(STDOUT_URI, YamlMediaType.YAML_UTF_8);
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            new TemplyMain().run(dataURIs, templateURI, outURI);
        }
    }

    public void run(Iterable<URI> dataURIs, URI templateURI, URI outURI)
            throws ConversionException, IOException {
        var templateResource = rp.getReadableResource(templateURI);
        var outResource = rp.getNonNull(outURI);

        var temply = new Temply();
        temply.convertIntoOrThrow(templateResource, outResource);
    }
}
