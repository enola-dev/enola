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

# This script install pre-requisite go tools

GO_BIN_PATH=$(go env GOPATH)/bin
BZL=$GO_BIN_PATH/bazelisk

go install github.com/bazelbuild/bazelisk@latest
go install github.com/bazelbuild/buildtools/buildifier@latest
go install github.com/bazelbuild/buildtools/buildozer@latest

# Due to https://github.com/salesforce/bazel-vscode-java/issues/88, like in
# https://github.com/vorburger/vorburger-dotfiles-bin-etc/blob/
# 64d3854b40f57183c81a0c9e054bafcbe3026ff7/all-install.sh#L66
ln -s "$BZL" "$GO_BIN_PATH"/bazel
ln -s "$BZL" "$GO_BIN_PATH"/b

"$BZL" version
