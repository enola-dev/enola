package dev.enola.core.meta;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.MessageValidators;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class EntityKindRepository {

    private final MessageValidators v = EntityKindValidations.INSTANCE;

    private final Map<String, Map<String, EntityKind>> map = new TreeMap<>();

    public synchronized void put(EntityKind entityKind) throws ValidationException {
        v.validate(entityKind).throwIt();
        // TODO This should eventually not be required anymore!
        // (Validation should "recurse into" messages by itself, later.)
        v.validate(entityKind.getId()).throwIt();

        var id = entityKind.getId();
        map.computeIfAbsent(id.getNs(), s -> new TreeMap<>()).put(id.getEntity(), entityKind);
    }

    public synchronized EntityKind get(ID id) {
        var subMap = map.get(id.getNs());
        if (subMap == null) {
            throw new IllegalArgumentException(id + " unknown: " + map);
        }
        return subMap.get(id.getEntity());
    }

    public void load(ReadableResource resource) throws IOException, ValidationException {
        var kinds = new ProtoIO().read(resource, EntityKinds.newBuilder()).build();
        for (var kind: kinds.getKindsList()) {
            put(kind);
        }
    }

    public Collection<EntityKind> list() {
        var eks = new ArrayList<EntityKind>();
        map.values().forEach(innerMap -> innerMap.values().stream().forEach(eks::add));
        return eks;
    }

    public Collection<ID> listID() {
        var ids = new ArrayList<ID>();
        map.values().forEach(innerMap -> innerMap.values().stream().map(EntityKind::getId).forEach(ids::add));
        return ids;
    }
}
