#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# TODO Transform this into a Bazel target instead?
# Or an enola exec: resource?
# (Or no need, for either?)
set -euox pipefail

# BEWARE: This may well be a different version than the one used by Bazel!
# The com.google.protobuf.MapFieldReflectionAccessor problem is related to that.
protoc --version

# Writting into java/ is actually a Very Bad Idea, as this then BREAKS the Bazel build.
protoc --java_out=java/ \
  java/dev/enola/common/protobuf/test.proto \
  java/dev/enola/thing/thing.proto \
  java/dev/enola/core/meta/enola_meta.proto \
  java/dev/enola/core/enola_core.proto \
  java/dev/enola/common/protobuf/validation.proto

# TODO This would also need to do the gRPC code generation...

# TODO Use Gradle?
