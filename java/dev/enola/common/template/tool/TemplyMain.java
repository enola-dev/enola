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
package dev.enola.common.template.tool;

import static dev.enola.common.io.resource.FileDescriptorResource.STDOUT_URI;

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.jackson.JacksonObjectReaderWriterChain;
import dev.enola.common.io.object.template.TemplatedObjectReader;
import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.template.TemplateProvider;
import dev.enola.common.template.handlebars.HandlebarsTemplateProvider;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

// This is used by //MODULE.update.bash
public class TemplyMain {

    // TODO Expose this as an ./enola template CLI sub-command too

    private final Temply temply;
    private final ResourceProvider rp;

    public TemplyMain(
            ResourceProvider rp,
            ObjectReader delegateObjectReader,
            TemplateProvider templateProvider) {
        this.rp = rp;
        this.temply = new Temply(new TemplatedObjectReader(delegateObjectReader), templateProvider);
    }

    private static ResourceProvider rp() {
        var fileRP = new FileResource.Provider();
        var fdRP = new FileDescriptorResource.Provider();
        return new ResourceProviders(fileRP, fdRP);
    }

    private static final TemplyMain INSTANCE =
            new TemplyMain(
                    rp(), new JacksonObjectReaderWriterChain(), new HandlebarsTemplateProvider());

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            var use = "USAGE: temply [data1.yaml data2.json ...] <template.handlebars>";
            System.err.println(use);
            System.exit(1);
        }
        var dataURIs = Arrays.stream(args, 0, args.length - 1).map(URI::create).toList();
        var templateURI = URI.create(args[args.length - 1]);

        MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes());
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            INSTANCE.run(dataURIs, templateURI, STDOUT_URI);
        }
    }

    public void run(List<URI> dataURIs, URI templateURI, URI outURI) throws IOException {
        var dataResources = dataURIs.stream().map(rp::getNonNull2).toList();
        var templateResource = rp.getNonNull(templateURI);
        var outResource = rp.getNonNull(outURI);

        temply.convert(dataResources, templateResource, outResource);
    }
}
