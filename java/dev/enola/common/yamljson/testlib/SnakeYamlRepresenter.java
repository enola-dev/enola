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
package dev.enola.common.yamljson.testlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

class SnakeYamlRepresenter extends Representer {

    SnakeYamlRepresenter(DumperOptions options) {
        super(options);
        this.representers.put(URI.class, new URIRepresentation());
    }

    @Override
    protected NodeTuple representJavaBeanProperty(
            Object javaBean, Property property, Object propertyValue, Tag customTag) {
        // Skip null fields
        if (propertyValue == null) return null;
        if (propertyValue instanceof Collection collection && collection.isEmpty()) return null;
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        // Skip Java Class Name !! tags
        if (!classTags.containsKey(javaBean.getClass())) addClassTag(javaBean.getClass(), Tag.MAP);
        return super.representJavaBean(properties, javaBean);
    }

    // skipcq: JAVA-W1019
    private class URIRepresentation implements Represent {
        @Override
        public Node representData(Object data) {
            URI uri = (URI) data;
            return representScalar(Tag.STR, uri.toString());
        }
    }
}
