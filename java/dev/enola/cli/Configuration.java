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
package dev.enola.cli;

import dev.enola.common.context.Singleton;
import dev.enola.common.io.mediatype.MarkdownMediaTypes;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.xml.XmlMediaType;
import dev.enola.format.tika.TikaMediaTypeProvider;
import dev.enola.infer.datalog.DatalogMediaTypes;
import dev.enola.rdf.io.RdfMediaTypeYamlLd;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.gen.gexf.GexfMediaType;
import dev.enola.thing.gen.graphcommons.GraphCommonsMediaType;
import dev.enola.thing.gen.graphviz.GraphvizMediaType;
import dev.enola.thing.io.ThingMediaTypes;

class Configuration {

    // TODO Use https://github.com/google/guava/wiki/NewCollectionTypesExplained#classtoinstancemap

    private static final MediaTypeProviders MTP =
            new MediaTypeProviders(
                    // NB: The order in which we list the MediaTypeProvider here matters!
                    new ThingMediaTypes(),
                    new RdfMediaTypes(),
                    new RdfMediaTypeYamlLd(),
                    new ProtobufMediaTypes(),
                    new MarkdownMediaTypes(),
                    new GraphvizMediaType(),
                    new GexfMediaType(),
                    new GraphCommonsMediaType(),
                    new DatalogMediaTypes(),
                    new StandardMediaTypes(),
                    new YamlMediaType(),
                    new XmlMediaType(),
                    new TikaMediaTypeProvider());

    public static void setSingletons() {
        MediaTypeProviders.SINGLETON.set(Configuration.MTP);
    }

    public static Singleton<?>[] singletons() {
        return new Singleton[] {MediaTypeProviders.SINGLETON};
    }
}
