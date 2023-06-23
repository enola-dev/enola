package dev.enola.core.meta;

import dev.enola.core.EntityAspect;

/** Poor man's Dependency Inject (DI). */
public interface EntityAspectWithRepository extends EntityAspect {
    void setEntityKindRepository(EntityKindRepository ekr);
}
