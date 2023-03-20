package dev.enola.common.protobuf;

import com.google.protobuf.Descriptors;import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Timestamp;

import dev.enola.common.validation.Validations;

@FunctionalInterface
public interface MessageValidator<T extends MessageOrBuilder> {

    void validate(T m, MessageValidators.Result.Builder r);
}
