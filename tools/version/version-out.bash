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

# Inspired e.g. by https://github.com/palantir/gradle-git-version

VERSION=$(git describe --tags --always --first-parent)
VERSION=${VERSION%$'\n'}

# Check for uncommitted files (that are not in .gitignore)
# Beware that this could cause too frequent rebuilds during development while commiting;
# but it's fine here because we only get this far if we are not on $CI anyway, so all good.
if [ -n "$(git status --porcelain)" ]; then
  # Use -dirty instead of .dirty so that it's in the same format as Nix's self.dirtyShortRev
  VERSION="${VERSION}-dirty"
fi

echo "$VERSION"
