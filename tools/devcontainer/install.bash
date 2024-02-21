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

set -euo pipefail

# This script prepares the Dev Container used for GitHub CodeSpaces.
# Because this is invoked as an onCreateCommand in the .devcontainer/devcontainer.json,
# this should very fast (and e.g. much faster than the full-blown ../devenv/install.bash),
# and only install minimal require basic tools, to let developers enter the codespace relatively quickly.

ROOT_DIR=$(realpath "$(dirname "$0")")/../..

"$ROOT_DIR"/tools/asdf/install.bash

# Since we do not install Java via ASDF by default (see PS in .tools-versions),
# we do that here now - but notably only for the dev container. And we do a "global"
# installation, to prevent this from being added to the project's .tools-versions.
asdf plugin add java
asdf install temurin-21.0.2+13.0.LTS
asdf global java temurin-21.0.2+13.0.LTS
java --version

# This is required, otherwise when opening *.java it will be all red;
# see /usr/local/bin/bazel related inline documentation in tools/go/install.bash.
"$ROOT_DIR"/tools/go/install.bash
