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

tools/javac/dependencies.bash

mkdir -p generated/classpath
mvn eu.maveniverse.maven.plugins:toolbox:gav-classpath -Dgav=docs/dev/dependencies.txt -DextraRepositories=jitpack::https://jitpack.io -q -DforceStdout >generated/classpath/enola.classpath

ENOLA_CLASSPATH=$(cat generated/classpath/enola.classpath)
IFS=':' read -ra JAR_PATHS <<< "$ENOLA_CLASSPATH"
mkdir -p generated/classpath/enola
for JAR in "${JAR_PATHS[@]}"; do
    if [[ "$JAR" == *.jar ]]; then
        cp "$JAR" generated/classpath/enola
    else
        echo "NOT a JAR: $JAR"
    fi
done
