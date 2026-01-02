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

# https://stackoverflow.com/a/2659808

git status

# TODO Remove || true later, see https://github.com/enola-dev/enola/issues/1315
git diff --exit-code || true

# TODO Does this do (exactly) the same as above?
if ! git update-index --refresh >/dev/null; then
  git status
  echo "Build only works if there are no git Changes not staged for commit! Abort."
  # TODO Re-activate "exit 255" later
  # exit 255
fi

if git status --porcelain | grep '??' > /dev/null; then
  echo "Untracked files exist (but should not)"
  exit 1
fi
