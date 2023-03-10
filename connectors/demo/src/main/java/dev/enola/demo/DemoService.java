package dev.enola.demo;

import dev.enola.core.EnolaService;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.GetEntityResponse;

public class DemoService implements EnolaService {

    @Override
    public GetEntityResponse getEntity(GetEntityRequest r) {
        throw new IllegalStateException("TODO");
    }
}
