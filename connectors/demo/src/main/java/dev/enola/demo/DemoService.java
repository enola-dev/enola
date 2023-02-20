package dev.enola.demo;

import dev.enola.core.EnolaService;
import dev.enola.core.proto.GetRequest;
import dev.enola.core.proto.GetResponse;
import dev.enola.core.proto.QueryAvailableEntitiesRequest;
import dev.enola.core.proto.QueryAvailableEntitiesResponse;

public class DemoService implements EnolaService {

    @Override
    public GetResponse get(GetRequest r) {
        return null; // TODO
    }

    @Override
    public QueryAvailableEntitiesResponse queryAvailableEntities(QueryAvailableEntitiesRequest r) {
        return null; // TODO
    }
}