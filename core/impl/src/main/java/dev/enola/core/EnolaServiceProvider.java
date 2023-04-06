package dev.enola.core;

import dev.enola.core.meta.EntityKindRepository;

public class EnolaServiceProvider {

    public EnolaService get(EntityKindRepository ekr) {
        var r = new EnolaServiceRegistry();
        for (var ek : ekr.list()) {
            var s = new EntityAspectService();

            r.register(ek.getId(), s);
        }
        return r;
    }
}
