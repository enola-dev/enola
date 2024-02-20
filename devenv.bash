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

# This script builds the project using containers technology (AKA "Docker").

docker build -t enola.dev-devenv --rm -f Dockerfile-DevEnv .

# NB: Use PWD instead of . here because different of different . intepretations;
# between older/newer Docker (and Podman...) versions - at least some older Docker
# versions have been observed to fail, because they assume . to be a volume name! :/)
set +u
if [ -z "$CI" ]; then
  docker run -v "$PWD":/workspace/:Z -it --rm enola.dev-devenv
fi
set -u
