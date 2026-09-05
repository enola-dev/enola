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

tools/maven/install.bash

# Check that exported modules do not depend on un-exported internal dev.enola modules
EXPORTED_TARGETS=$(bazel query 'kind("maven_project_jar", //java/dev/enola/...) except //java/dev/enola:enola-project')
for TARGET in $EXPORTED_TARGETS; do
    UNEXPORTED=$(bazel query "(kind(\"java_library\", deps(${TARGET})) intersect //java/dev/enola/...) except attr(tags, \"maven_coordinates=\", //java/dev/enola/...)")
    if [ -n "${UNEXPORTED}" ]; then
        echo "ERROR: Exported target ${TARGET} depends on internal un-exported module(s):"
        echo "${UNEXPORTED}"
        echo "All internal dev.enola dependencies of an exported module must also be exported via export_maven."
        exit 1
    fi
done

# Note that the JBang version is not fixed, and could change. This is *normally* fine, as it's supposed to backwards compatible.
# In the past we used to "pin" it here (via "rm -rf ~/.jbang/ && JBANG_DOWNLOAD_VERSION=0.122.0 learn/jbang/jbang ...),
# due to https://github.com/enola-dev/enola/issues/1040, but since that's been resolved, we don't anymore now.)
learn/jbang/jbang --version

learn/jbang/jbang learn/jbang/common.java

learn/jbang/jbang learn/jbang/hello.java

learn/jbang/jbang learn/jbang/enola.java --help
