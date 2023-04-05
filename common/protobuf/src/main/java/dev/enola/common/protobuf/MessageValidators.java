/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.common.protobuf;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageOrBuilder;
import dev.enola.common.validation.Validation;
import dev.enola.common.validation.Validations;
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
        result.addValidations(
            Validation.newBuilder().setPath(descriptor.getName()).setError(error).build());
      }
    }
  }
}
