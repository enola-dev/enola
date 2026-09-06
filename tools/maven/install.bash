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

# This installs all exported Maven Artifacts into the local repository (~/.m2/repository/dev/enola/)

bazel build //java/dev/enola/...

tools/javadoc/build.bash

TARGETS=$(bazel query 'kind("maven_project_jar", //java/dev/enola/...)')

for TARGET in $TARGETS; do
    PKG="${TARGET#//}"
    PKG="${PKG%%:*}"
    TARGET_NAME="${TARGET##*:}"
    BASE="${TARGET_NAME%-project}"

    JAR="bazel-bin/${PKG}/${BASE}-project.jar"
    SOURCES="bazel-bin/${PKG}/${BASE}-project-src.jar"
    POM="bazel-bin/${PKG}/${BASE}-pom.xml"

    mvn install:install-file \
        -Dfile="${JAR}" \
        -Dsources="${SOURCES}" \
        -DpomFile="${POM}" \
        -Djavadoc=.built/javadoc.jar
done
