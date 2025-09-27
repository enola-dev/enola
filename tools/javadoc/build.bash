#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

set -euox pipefail

# FTR: We're using this instead of @rules_jvm_external' javadoc rule because:
#   - That one includes com.google.protobuf & io.grpc & javax.annotation - WTF?!
#   - https://github.com/bazel-contrib/rules_jvm_external/issues/1343
#   - https://github.com/bazel-contrib/rules_jvm_external/issues/1344

ENOLA_CLASSPATH=$(cat generated/classpath/enola.classpath)

find java/ generated/protoc/java/ -name "*.java" | grep -v Test.java | grep -v Tester.java > /tmp/enola-java-sources.txt

rm -rf site/dev/javadoc/

javadoc -Xdoclint:none -Werror -linksource -d site/dev/javadoc/ @/tmp/enola-java-sources.txt \
  -classpath "$ENOLA_CLASSPATH":bazel-bin/java/dev/enola/core/tests.runfiles/_main/java/dev/enola/thing/libthing_proto-speed.jar:bazel-bin/java/dev/enola/core/libcore_proto-speed.jar:bazel-bin/java/dev/enola/core/libcore_java_grpc.jar:bazel-bin/java/dev/enola/core/tests.runfiles/_main/java/dev/enola/common/protobuf/libvalidation_proto-speed.jar \
  -link https://guava.dev/releases/33.4.8-jre/api/docs/ \
  2>&1 | grep -v "Loading source file " | grep -v "Generating "

jar --create --file .built/javadoc.jar -C site/dev/javadoc/ .

# TODO -Xdoclint:all & -Werror

# TODO -link for all 3rd party libraries
