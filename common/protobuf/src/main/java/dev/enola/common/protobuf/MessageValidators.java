package dev.enola.common.protobuf;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageOrBuilder;

import dev.enola.common.validation.Validation;import dev.enola.common.validation.Validations;

import java.util.*;

public class MessageValidators {
    private final Map<Descriptors.Descriptor, List<MessageValidator<MessageOrBuilder>>> map =
            new HashMap<>();

    private static final Result EMPTY = Result.newBuilder().build();

    @CanIgnoreReturnValue
    public MessageValidators register(
            MessageValidator<?> validator, Descriptors.Descriptor descriptor) {
        map.computeIfAbsent(descriptor, descriptor1 -> new ArrayList<>())
                .add((MessageValidator<MessageOrBuilder>) validator);
        return this;
    }

    @CheckReturnValue
    public Result validate(MessageOrBuilder message) {
        var validators = map.get(message.getDescriptorForType());
        if (validators == null) {
            return EMPTY;
        }
        var results = Result.newBuilder();
        for (var validator : validators) {
            validator.validate(message, results);
        }
        return results.build();
    }

    public static class Result {
        private final Validations proto;

        private Result(Validations proto) {
            this.proto = proto;
        }

        @CheckReturnValue
        public Validations toMessage() {
            return proto;
        }

        public void throwIt() throws ValidationException {
            if (proto.getValidationsCount() > 0) {
                throw new ValidationException(proto);
            }
        }

        private static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private final Validations.Builder result = Validations.newBuilder();

            private Builder() {}

            Result build() {
                var proto = result.build();
                return new Result(proto);
            }

            public void add(Descriptors.GenericDescriptor descriptor, String error) {
                // TODO Refine setPath() ... it should "aggregate" from "parents" ...
                result.addValidations(Validation.newBuilder().setPath(descriptor.getName()).setError(error).build());
            }
        }
    }
}
