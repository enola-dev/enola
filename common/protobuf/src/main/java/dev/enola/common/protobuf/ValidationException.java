package dev.enola.common.protobuf;

import dev.enola.common.validation.Validations;

public class ValidationException extends Exception {
    private final Validations proto;

    public ValidationException(Validations proto) {
        this.proto = proto;
    }

    @Override
    public String toString() {
        return proto.toString();
    }
}
