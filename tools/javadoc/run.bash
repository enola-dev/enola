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

set -euo pipefail

if [[ $# -eq 0 ]]; then
  echo "Usage: $0 <source files> [<additional classpath entry>]" >&2
  exit 1
fi

# The last argument is an optional additional classpath entry;
# see e.g. tools/javadoc/pre-commit.bash
additional_classpath="${*: -1}"

# All arguments except the last are the Java source files to process;
# see e.g. tools/javadoc/build.bash
source_files=("${@:1:$#-1}")

ENOLA_CLASSPATH=$(cat generated/classpath/enola.classpath)

javadoc -linksource -d site/dev/javadoc/ "${source_files[@]}" \
  -classpath "${additional_classpath:+$additional_classpath:}$ENOLA_CLASSPATH":bazel-bin/java/dev/enola/core/tests.runfiles/_main/java/dev/enola/thing/libthing_proto-speed.jar:bazel-bin/java/dev/enola/core/libcore_proto-speed.jar:bazel-bin/java/dev/enola/core/libcore_java_grpc.jar:bazel-bin/java/dev/enola/core/tests.runfiles/_main/java/dev/enola/common/protobuf/libvalidation_proto-speed.jar \
  -link https://guava.dev/releases/33.4.8-jre/api/docs/ \
  -Werror -Xdoclint:all,-missing \
  -quiet 2>&1

# Please keep -Xdoclint in sync with .bazelrc

# TODO -link for all 3rd party libraries
