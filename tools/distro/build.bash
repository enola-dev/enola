#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023 The Enola <https://enola.dev> Authors
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

# TODO Transform this into a Bazel target instead of a 2 line shell script, to avoid 34 MB copy when not needed!
mkdir -p site/download/latest/
set -euox pipefail
cp tools/distro/execjar-header.bash site/download/latest/enola
cat bazel-bin/cli/enola_deploy.jar >>site/download/latest/enola
