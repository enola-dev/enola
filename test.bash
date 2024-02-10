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

# This script builds the project *WITHOUT* requiring containers.
# It can be used *IN* a container though; and is so, by the ./build script.

# Similar also in the ./enola script:
GO_BIN_PATH=$(go env GOPATH)/bin
BZL=$GO_BIN_PATH/bazelisk
if ! [ -x "$(command -v "$BZL")" ]; then
  if [ -x "$(command -v go)" ]; then
    tools/go/install.bash

  else
    echo "Please install Go from https://go.dev/doc/install and re-run this script!"
    echo "See also https://docs.enola.dev/dev/setup/"
    exit 255
  fi
fi

# https://github.com/bazelbuild/bazel/issues/4257
echo $ Bazel testing...
# TODO Remove --nojava_header_compilation when https://github.com/bazelbuild/bazel/issues/21119 is fixed
"$BZL" query //... | xargs "$BZL" test --nojava_header_compilation

# The following makes sure that this test.bash will run as a pre-commit hook.
# NB: We DO NOT want to "pre-commit install" because that won't run bazelisk!
# (And because our own venv etc. stuff above is better for the "first touch" contributor experience.)
# This is intentionally only done here at the END of successfully running the tests above,
# because only if we reach here we now that everything above actually works well locally.

source tools/pre-commit/install.bash

echo
# Run https://pre-commit.com, see .pre-commit-config.yaml;
# locally run only on last commit (quick), but on CI
# <https://stackoverflow.com/a/75223617/421602>
# do run on all files, even if a bit slower.
# This prevents "cheating" and tech debt.
set +u
if [ -z "$CI" ]; then
  echo "$ pre-commit run (locally, only changed files)"
  .venv/bin/pre-commit run
else
  echo "$ pre-commit run --all-files (on CI)"
  .venv/bin/pre-commit run --all-files
fi
set -u

tools/git/install-hooks.bash
