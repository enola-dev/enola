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

set -euo pipefail

# TODO Replace this with docs/use/**/BUILD files, so that demo tests only run if inputs change!

# Note use of xargs instead of find -exec \; for error handling, see https://apple.stackexchange.com/a/49047
find docs/use -maxdepth 1 -not -path docs/use -type d -print0 | xargs -n 1 -0 tools/demo/test.bash
