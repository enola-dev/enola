#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
shopt -s globstar

# TODO Must separate test from runtime classpath, must exclude certain artifacts

tools/javac/classpath.bash

ENOLA_CLASSPATH=$(cat generated/classpath/enola.classpath)

# TODO -proc:full --processor-path <path>

# TODO -Xlint:unchecked

# TODO -Xlint:deprecation

# TODO -Xdoclint:all/protected

# Keep options in sync with .bazelrc and tools/javadoc/run.bash
javac --class-path "$ENOLA_CLASSPATH" \
  --enable-preview -encoding UTF-8 -g -parameters \
  -d generated/java-class/ -s generated/javac-processors/ --source-path java/ \
  --source 21 --target 21 \
  -Werror \
  @<(find generated/protoc/java java -name "*.java")

# PS: For debugging, add: -verbose -XprintRounds

tools/javac/enola help
