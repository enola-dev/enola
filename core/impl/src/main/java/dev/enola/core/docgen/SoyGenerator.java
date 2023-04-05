/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core.docgen;

import com.google.common.net.MediaType;
import com.google.protobuf.Descriptors;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.parseinfo.TemplateName;
import dev.enola.common.io.resource.ClasspathResource;
import java.util.Collections;

class SoyGenerator {

  protected final SoySauce.Renderer renderer;

  public SoyGenerator(String classpathResource, String template) {
    // TODO Refactor this... it's an ugly initial hack!
    // var core_proto = new ClasspathResource("dev/enola/core/enola_core.proto",
    // MediaType.PLAIN_TEXT_UTF_8);
    var soy = new ClasspathResource(classpathResource, MediaType.PLAIN_TEXT_UTF_8);
    var sfs =
        SoyFileSet.builder()
            .add(soy.charSource(), soy.uri().toString())
            // .add(core_proto.charSource(), core_proto.uri().toString())
            .addProtoDescriptors(protoDescriptors())
            .build();
    var tofu = sfs.compileToTofu();
    var sauce = sfs.compileTemplates();
    renderer = sauce.renderTemplate(TemplateName.of(template));
  }

  protected Iterable<? extends Descriptors.GenericDescriptor> protoDescriptors() {
    return Collections.emptySet();
  }
}
