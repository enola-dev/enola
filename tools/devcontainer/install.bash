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

# This script prepares the Dev Container used for GitHub CodeSpaces.
# Because this is invoked as an onCreateCommand in the .devcontainer/devcontainer.json,
# this should very fast (and e.g. much faster than the full-blown ../devenv/install.bash),
# and only install minimal require basic tools, to let developers enter the codespace relatively quickly.

ROOT_DIR=$(realpath "$(dirname "$0")")/../..

# Nix Setup
# ToDo https://github.com/devcontainers/features/issues/1518: nix develop

# This is required, otherwise when opening *.java it will be all red;
# see /usr/local/bin/bazel related inline documentation in tools/go/install.bash.
# TODO Clean-up entirely....
# "$ROOT_DIR"/tools/go/install.bash
