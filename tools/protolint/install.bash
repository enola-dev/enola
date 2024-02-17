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

# protolint, for VSC plex.vscode-protolint extension.
# NB: Pre-Commit installs it's own version; this install is only for the IDE.

# TODO Install protolint via .tools.versions instead of with this
# when https://github.com/spencergilbert/asdf-protolint/issues/37 is fixed

if ! [ -x "$(command -v "$(go env GOPATH)/bin/protolint")" ]; then
  # This version must be kept in the sync with the one in .pre-commit-config.yaml
  go install github.com/yoheimuta/protolint/cmd/protolint@v0.47.5
fi
