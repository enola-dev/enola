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

set -euox pipefail

# This script runs on CI and tests the project.

# shellcheck source=/dev/null
source tools/asdf/install.bash

rm -rf docs/models/

# TODO Run all this only when model inputs change
tools/protoc/protoc.bash

# This writes into docs/models/ (which is on .gitignore), not site/ (which mkdocs cleans when it starts; à la rm -rf site/)
models/build.bash

./test.bash

# No files (which are not on .gitigore) should have been modified!
tools/git/test.bash

# Test distros: 1. End-user distributed executable fat über JAR, 2. Container Image
tools/distro/test.bash
