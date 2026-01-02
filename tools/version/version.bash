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

# This is a PITA (!) during development, because every change and commit on //web/
# triggers a full rebuild of //java/. We therefore now only run this on CI:
if [ -z "${CI:-""}" ]; then
  exit 0
fi

# It's *VERY* important that this script *ONLY* touches the tools/version/VERSION file
# when its content actually changed. Otherwise it triggers a frequent full rebuild. This is
# because Bazel (also?) looks at the timestamp of the file to determine if it needs
# to rebuild, not ([only?] a hash of) its content.

NEW_VERSION=$(tools/version/version-out.bash)

if [ ! -f tools/version/VERSION ] || [ "$(cat tools/version/VERSION)" != "$NEW_VERSION" ]; then
  echo -n "$NEW_VERSION" > tools/version/VERSION
fi
