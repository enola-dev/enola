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

if ! command -v go &> /dev/null; then
  echo "Please install Go from https://go.dev/doc/install and re-run this script!"
  echo "See also https://docs.enola.dev/dev/setup/"
  exit 255
fi

# This script install pre-requisite go tools
echo "$PATH"
GO_BIN_PATH=$(go env GOPATH)/bin
BZL=$GO_BIN_PATH/bazelisk

go install github.com/bazelbuild/bazelisk@v1.19.0
# TODO https://github.com/bazelbuild/buildtools/issues/1237
# We should install a fixed version? @v6.4.0 doesn't work:
# 'invalid version: module contains a go.mod file, so module path must match major version
# ("github.com/bazelbuild/buildtools/v6")', but "go install github.com/bazelbuild/buildtools/buildifier@v6"
# also fails, with: 'go: github.com/bazelbuild/buildtools/buildifier@v6: no matching versions for query "v6"'
go install github.com/bazelbuild/buildtools/buildifier@latest
go install github.com/bazelbuild/buildtools/buildozer@latest

# Due to https://github.com/salesforce/bazel-vscode-java/issues/88, like in
# https://github.com/vorburger/vorburger-dotfiles-bin-etc/blob/
# 64d3854b40f57183c81a0c9e054bafcbe3026ff7/all-install.sh#L66
ln -fs "$BZL" "$GO_BIN_PATH"/bazel
ln -fs "$BZL" "$GO_BIN_PATH"/b

# The Language Server from the VSC Bazel Java extension
# needs to be able to launch "bazel" from PATH - and that only works if it's in a
# directory that's on the OS default PATH, such as e.g. in /usr/local/bin/ - but
# VSC (Web) WON'T WORK if bazelisk is in some place like ~/go/bin/ or wherever;
# see https://github.com/salesforce/bazel-vscode-java/issues/94
# (and //.devcontainer/devcontainer.json) for further background.
set +u
if [ -n "$CODESPACES" ]; then
  sudo ln -fs "$BZL" /usr/local/bin/bazelisk
  sudo ln -fs "$BZL" /usr/local/bin/bazel
fi
set -u

"$BZL" version
