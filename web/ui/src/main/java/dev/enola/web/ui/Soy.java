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
package dev.enola.web.ui;

import com.google.common.net.MediaType;
import com.google.protobuf.Descriptors;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.parseinfo.TemplateName;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;

import java.util.Map;

public class Soy {

    // TODO Consider using closure_java_template_library Bazel rule?
    // See https://github.com/bazelbuild/rules_closure/#closure_java_template_library.
    // But must first contribute https://github.com/bazelbuild/rules_closure/issues/225;
    // this won't work without that.

    private final SoyFileSet sfs;
    private final SoySauce sauce;

    private Soy(SoyFileSet sfs) {
        this.sfs = sfs;
        this.sauce = sfs.compileTemplates();
    }

    public SoySauce.Renderer newRenderer(String templateName, Map<String, ?> params) {
        return sauce.renderTemplate(TemplateName.of(templateName)).setData(params);
    }

    public static class Builder {
        SoyFileSet.Builder sfsb = SoyFileSet.builder();

        Builder addSoy(ReadableResource r) {
            sfsb.add(r.charSource(), r.uri().toString());
            return this;
        }

        public Builder addSoy(String classpath) {
            addSoy(new ClasspathResource(classpath, MediaType.PLAIN_TEXT_UTF_8));
            return this;
        }

        public Builder addProto(Descriptors.GenericDescriptor... descriptors) {
            sfsb.addProtoDescriptors(descriptors);
            return this;
        }

        public Soy build() {
            return new Soy(sfsb.build());
        }
    }
}
