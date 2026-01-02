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

set -euo pipefail

tools/javac/dependencies.bash

mkdir -p generated/classpath
mvn eu.maveniverse.maven.plugins:toolbox:gav-classpath -Dgav=docs/dev/dependencies.txt -DextraRepositories=jitpack::https://jitpack.io -q -DforceStdout >generated/classpath/enola.classpath

ENOLA_CLASSPATH_DIR=generated/classpath/enola
rm -rf "${ENOLA_CLASSPATH_DIR:?}"/*
mkdir -p "$ENOLA_CLASSPATH_DIR"

ENOLA_CLASSPATH=$(cat "$ENOLA_CLASSPATH_DIR.classpath")
IFS=':' read -ra JAR_PATHS <<< "$ENOLA_CLASSPATH"

MAVEN_REPO_PATH="$HOME/.m2/repository"
for JAR in "${JAR_PATHS[@]}"; do
    if [[ "$JAR" == *.jar ]]; then
        if [[ "$JAR" == "$MAVEN_REPO_PATH"* ]]; then
          RELATIVE_PATH="${JAR#"$MAVEN_REPO_PATH"/}"
          FILENAME="${RELATIVE_PATH##*/}"
          VERSION_PATH="${RELATIVE_PATH%/*}"
          BASE_PATH_FULL="${VERSION_PATH%/*}"
          GROUP_PATH="${BASE_PATH_FULL%/*}"
          MODIFIED_RELATIVE_PATH="$GROUP_PATH/$FILENAME"
          NEW_FILENAME="${MODIFIED_RELATIVE_PATH//\//_}"
          cp "$JAR" "$ENOLA_CLASSPATH_DIR/$NEW_FILENAME"
        else
          cp "$JAR" "$ENOLA_CLASSPATH_DIR"
        fi
    else
      echo "NOT a JAR: $JAR"
    fi
done
