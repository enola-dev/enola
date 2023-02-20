package dev.enola.core;

import dev.enola.core.proto.GetRequest;
import dev.enola.core.proto.GetResponse;
import dev.enola.core.proto.QueryAvailableEntitiesRequest;
import dev.enola.core.proto.QueryAvailableEntitiesResponse;

public interface EnolaService {
    GetResponse get(GetRequest r);
    QueryAvailableEntitiesResponse queryAvailableEntities(QueryAvailableEntitiesRequest r);
}
