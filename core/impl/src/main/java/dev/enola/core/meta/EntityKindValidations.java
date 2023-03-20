package dev.enola.core.meta;

import com.google.common.base.Strings;

import dev.enola.common.protobuf.MessageValidator;
import dev.enola.common.protobuf.MessageValidators;import dev.enola.core.proto.ID;

public class EntityKindValidations {

    private static MessageValidator<ID> id =
            (m, r) -> {
                // TODO Validate that NS matches regexp as per enola_core.proto

                 if (Strings.isNullOrEmpty(m.getEntity())) {
                    // TODO Simplify making fields required
                    r.add(ID.getDescriptor(), "mandatory");
                } else {
                    // TODO Validate that entity name matches regexp as per enola_core.proto
                }

                // TODO Validate that paths all match regexp as per enola_core.proto
            };

    public static MessageValidators INSTANCE = new MessageValidators()
        .register(id, ID.getDescriptor());
}
