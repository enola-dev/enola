package dev.enola.core;

import dev.enola.core.proto.Entity;

interface EntityAspect {

    void augment(Entity.Builder entity);
}
