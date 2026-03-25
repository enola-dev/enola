#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

SOURCES_FILE=$(mktemp)
trap 'rm -f "$SOURCES_FILE"' EXIT
find java/ generated/protoc/java/ -name "*.java" | grep -v Test.java | grep -v Tester.java > "$SOURCES_FILE"

rm -rf site/dev/javadoc/

tools/javadoc/run.bash "@$SOURCES_FILE" ""

jar --create --file .built/javadoc.jar -C site/dev/javadoc/ .
