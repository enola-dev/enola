package dev.enola.core;

import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.GetEntityResponse;

import java.util.ArrayList;
import java.util.List;

class EntityAspectService implements EnolaService {

    private final List<EntityAspect> registry = new ArrayList<>();

    public void add(EntityAspect aspect) {
        registry.add(aspect);
    }

    @Override
    public GetEntityResponse getEntity(GetEntityRequest r) {
        var entity = Entity.newBuilder();
        entity.setId(r.getId());

        for (var aspect : registry) {
            aspect.augment(entity);
        }

        var response = GetEntityResponse.newBuilder().setEntity(entity).build();
        return response;
    }
}
