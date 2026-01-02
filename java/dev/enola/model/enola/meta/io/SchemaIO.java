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
package dev.enola.model.enola.meta.io;

import dev.enola.common.context.TLC;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.yamljson.YAML;
import dev.enola.data.iri.namespace.repo.EmptyNamespaceRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryBuilder;
import dev.enola.model.enola.HasName;
import dev.enola.model.enola.meta.*;
import dev.enola.model.enola.meta.Class;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SchemaIO {

    // TODO Use dev.enola.thing.validation and errors.esch.yaml instead of IllegalArgumentException

    public Schema readYAML(ReadableResource resource) throws IOException {
        // TODO Avoid AtomicReference
        AtomicReference<Schema> schema = new AtomicReference<>();
        readYAML(resource, schema1 -> schema.set(schema1));
        return schema.get();
    }

    public void readYAML(ReadableResource resource, Consumer<Schema> schemay) throws IOException {
        var repo = new MetaThingByIdProvider.Builder();
        YAML.readSingleMap(
                resource, map -> readSchema(map, repo, schema -> schemay.accept(schema)));
    }

    private void readSchema(
            Map<?, ?> map, MetaThingByIdProvider.Builder repo, Consumer<Schema> schemay) {
        var id = getRemoveString(map, "id");
        if (id == null) throw new IllegalArgumentException("Mandatory schema id: is missing");
        var schema = repo.schema(id);
        schema.name(getRemoveString(map, "name"));

        var iri = getRemoveString(map, "iri");
        if (iri == null) throw new IllegalArgumentException("Mandatory schema iri: is missing");
        if (iri.endsWith("/"))
            throw new IllegalArgumentException("Schema's iri: must NOT end with slash: " + iri);
        schema.iri(iri);
        // TODO Re-review this pretty ugly "hack" later...
        TLC.get(ThingRepositoryStore.class).store(schema);

        schema.description(getRemoveString(map, "description"));
        schema.java_package(getRemoveString(map, "java:package"));

        var nsr = namespaceRepository(map);
        // TODO Push onto TLC, and use when encountering namespaced IDs?

        // TODO imports

        var datatypes = getRemoveMap(map, "datatypes");
        if (datatypes != null) readDatatypes(datatypes, schema, repo);

        var properties = getRemoveMap(map, "properties");
        if (properties != null) readProperties(properties, schema, repo);

        var classes = getRemoveMap(map, "classes");
        if (classes != null) readClasses(classes, schema, repo);

        checkEmpty(map);
        schemay.accept(schema.build());
    }

    private NamespaceRepository namespaceRepository(Map<?, ?> map) {
        var prefixes = getRemoveMap(map, ".prefixes");
        if (prefixes == null) return EmptyNamespaceRepository.INSTANCE;
        var nsrb = new NamespaceRepositoryBuilder();
        prefixes.forEach((prefix, iri) -> nsrb.store(prefix.toString(), iri.toString()));
        return nsrb.build();
    }

    private void readClasses(
            Map<?, ?> map, Schema.Builder<?> schema, MetaThingByIdProvider.Builder repo) {
        readMaps(
                map,
                schema,
                (name, classMap) -> {
                    var clazz = repo.clazz(schema, name);
                    readCommon(classMap, clazz, schema.iri());

                    clazz.iriTemplate(getRemoveString(classMap, "iri_template"));

                    var parentsNames = getRemoveList(classMap, "parents");
                    if (parentsNames != null)
                        for (var parentName : parentsNames)
                            clazz.addParent(repo.clazz(schema, parentName.toString()));

                    var idPropertiesMap = asMap(getRemoveMap(classMap, "ids"));
                    if (idPropertiesMap != null)
                        readClassProperties(idPropertiesMap, clazz, schema, repo, true);

                    var propertiesMap = asMap(getRemoveMap(classMap, "properties"));
                    if (propertiesMap != null)
                        readClassProperties(propertiesMap, clazz, schema, repo, false);

                    checkEmpty(classMap);
                    schema.addSchemaClass(clazz);
                });
    }

    private void readDatatypes(
            Map<?, ?> map, Schema.Builder<?> schema, MetaThingByIdProvider.Builder repo) {
        readMaps(
                map,
                schema,
                (name, datatypeMap) -> {
                    var datatype = repo.datatype(schema, name);
                    readCommon(datatypeMap, datatype, schema.iri());
                    datatype.java(getRemoveString(datatypeMap, "java:type"));
                    datatype.proto(getRemoveString(datatypeMap, "proto"));

                    var xsd = getRemoveString(datatypeMap, "xsd");
                    // TODO Resolve CURIE, if it is one - but how do we know?
                    if (xsd != null) datatype.xsd(URI.create(xsd));

                    var parentName = getRemoveString(datatypeMap, "parent");
                    if (parentName != null) datatype.parent(repo.datatype(schema, parentName));

                    checkEmpty(datatypeMap);
                    schema.addSchemaDatatype(datatype);
                });
    }

    private void readProperties(
            Map<?, ?> map, Schema.Builder<?> schema, MetaThingByIdProvider.Builder repo) {
        readMaps(
                map,
                schema,
                (name, propertyMap) -> {
                    var property = repo.property(schema, name);
                    readProperty(propertyMap, property, schema, repo);
                    schema.addSchemaProperty(property);
                });
    }

    private void readClassProperties(
            Map<?, ?> map,
            Class.Builder<?> clazz,
            Schema schema,
            MetaThingByIdProvider.Builder repo,
            boolean isID) {
        map.forEach(
                (name, value) -> {
                    if (value == null)
                        // TODO throw new IllegalStateException("TODO Implement Property lookup: " +
                        // name);
                        ;
                    else {
                        var property = repo.property(schema, name.toString());
                        if (value instanceof Map<?, ?> propertyMap)
                            readProperty(propertyMap, property, schema, repo);
                        else if (value instanceof String propertyDatatypeName)
                            setDatatype(property, propertyDatatypeName, repo);
                        else throw new IllegalArgumentException(value.toString());
                        // TODO property.multiplicity(...)
                        if (isID) clazz.addClassIdProperty(property);
                        else clazz.addClassProperty(property);
                    }
                });
    }

    private void setDatatype(
            Property.Builder<?> property, String datatypeName, MetaThingByIdProvider.Builder repo) {
        property.datatype(repo.datatype(property.schema(), datatypeName));
    }

    private void readProperty(
            Map<?, ?> map,
            Property.Builder<?> property,
            Schema schema,
            MetaThingByIdProvider.Builder repo) {
        readCommon(map, property, schema.iri());
        setDatatype(property, getRemoveString(map, "type"), repo);
        var parentName = getRemoveString(map, "parent");
        if (parentName != null) property.parent(repo.property(schema, parentName));
        // TODO getRemoveString(map, "inverse")
        checkEmpty(map);
    }

    private void readCommon(Map<?, ?> map, Common.Builder<?> common, String schemaIRI) {
        common.iri(getRemoveString(map, "iri"));
        setIRI(common, schemaIRI);

        common.label(getRemoveString(map, "label"));
        common.description(getRemoveString(map, "description"));
        // TODO Handle description-md VS description...
        common.description(getRemoveString(map, "description-md"));
        // TODO Resolve enola:emoji vs label / description (without enola:) inconsistency...
        common.emoji(getRemoveString(map, "enola:emoji"));
    }

    private void readMaps(
            Map<?, ?> map, Schema.Builder<?> schema, BiConsumer<String, Map<?, ?>> mapper) {
        map.forEach(
                (name, object) -> {
                    var innerMap = asMap(object);
                    if (innerMap != null) mapper.accept(name.toString(), innerMap);
                });
    }

    // TODO Remove, since MetaThingByIdProvider already sets iri() ?
    private void setIRI(HasName.Builder named, String schemaIRI) {
        // NOT if (named.iri() == null) {
        named.iri(schemaIRI + "/" + named.name());
    }

    private /* TODO @Nullable */ String getRemoveString(Map<?, ?> map, String name) {
        var value = map.get(name);
        map.remove(name); // skipcq: JAVA-E1036
        return value != null ? value.toString() : null;
    }

    private @Nullable Map<?, ?> getRemoveMap(Map<?, ?> map, String name) {
        var value = map.get(name);
        map.remove(name); // skipcq: JAVA-E1036
        return asMap(value);
    }

    private @Nullable List<?> getRemoveList(Map<?, ?> map, String name) {
        var value = map.get(name);
        map.remove(name); // skipcq: JAVA-E1036
        return asList(value);
    }

    private @Nullable Map<?, ?> asMap(@Nullable Object object) {
        if (object == null) return null;
        if (object instanceof Map<?, ?> map) return map;
        throw new IllegalArgumentException(
                "Should be Map, but is: " + object.getClass() + " " + object);
    }

    private @Nullable List<?> asList(@Nullable Object object) {
        if (object == null) return null;
        if (object instanceof List<?> list) return list;
        return List.of(object);
    }

    // TODO Remove when finally fully switching to Thing, where it's *OK* to have extra!
    private void checkEmpty(Map<?, ?> map) {
        // Remove a few "special" entries:
        map.remove("@context"); // skipcq: JAVA-E1036
        map.remove("$schema"); // skipcq: JAVA-E1036

        if (!map.isEmpty()) {
            throw new IllegalArgumentException("Unknown properties: " + map);
        }
    }
}
