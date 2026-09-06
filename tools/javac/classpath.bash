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

# Use -x because this script sometimes strangely fails on CI...
set -xeuo pipefail

echo "tools/javac/classpath.bash now running..."

tools/javac/dependencies.bash

mkdir -p generated/classpath
mvn eu.maveniverse.maven.plugins:toolbox:gav-classpath -Daether.dependencyCollector.bf.threads=1 -Dgav=docs/dev/dependencies.txt -DextraRepositories=jitpack::https://jitpack.io -q -DforceStdout >generated/classpath/enola.classpath

ENOLA_CLASSPATH_DIR=generated/classpath/enola
rm -rf "${ENOLA_CLASSPATH_DIR:?}"/*
mkdir -p "$ENOLA_CLASSPATH_DIR"

ENOLA_CLASSPATH=$(cat "$ENOLA_CLASSPATH_DIR.classpath")
IFS=':' read -ra ALL_JAR_PATHS <<< "$ENOLA_CLASSPATH"
JAR_PATHS=()
for JAR in "${ALL_JAR_PATHS[@]}"; do
  if [[ "$JAR" == *"/io/modelcontextprotocol/sdk/mcp/"* ]] \
    || [[ "$JAR" == *"/ch/qos/logback/"* ]] \
    || [[ "$JAR" == *"/org/openjdk/nashorn/"* ]] \
    || [[ "$JAR" == *"/commons-logging/commons-logging/"* ]] \
    || [[ "$JAR" == *"/spring-boot-starter-logging/"* ]] \
    || [[ "$JAR" == *"/com/rometools/rome-utils/"* ]] \
    || [[ "$JAR" == *"/org/eclipse/jdt/ecj/"* ]]; then
    continue
  fi
  JAR_PATHS+=("$JAR")
done

printf -v ENOLA_CLASSPATH '%s:' "${JAR_PATHS[@]}"
ENOLA_CLASSPATH="${ENOLA_CLASSPATH%:}"
echo "$ENOLA_CLASSPATH" >"$ENOLA_CLASSPATH_DIR.classpath"

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
