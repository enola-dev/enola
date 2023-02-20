package dev.enola.core;

import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.GetEntityResponse;

public interface EnolaService {
    GetEntityResponse getEntity(GetEntityRequest r);
}
