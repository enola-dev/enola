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

# This script builds the project *WITHOUT* requiring containers.
# It can be used *IN* a container though; and is so, by the ./build script.

# Run Nix tools:
#   - https://github.com/oppiliappan/statix
#   - https://github.com/astro/deadnix
# BUT only on our own flake.nix; NOT on node_modules/ etc.
statix check flake.nix
deadnix --fail flake.nix

# Abort if there are any broken symlinks
(find . -xtype l -ls | grep .) && echo "Broken symlinks! Try: bazel clean --expunge" && exit 1

# TODO https://github.com/enola-dev/enola/issues/1789
#if command -v bazel &> /dev/null; then
#  BZL=bazel
# elif...
if command -v bazelisk &> /dev/null; then
  BZL=bazelisk
else
  if ! command -v go &> /dev/null; then
    echo "Error: 'go' command not found, and 'bazelisk' is not in PATH." >&2
    echo "Please install Go (see https://go.dev/doc/install) or set up your environment with Nix." >&2
    exit 1
  fi
  GO_BIN_PATH=$(go env GOPATH)/bin
  BZL=$GO_BIN_PATH/bazelisk
  if ! [ -x "$BZL" ]; then
    tools/go/install.bash
  fi
fi

tools/javac/dependencies.bash

tools/version/version.bash

tools/protoc/protoc.bash

# TODO Remove this once evilurl is Bazel test BUILD integrated...
tools/evilurl/test.bash

# TODO Replace skipping non-small tests and JavaDoc unless on CI with Bazel profiles!
# https://github.com/bazelbuild/bazel/issues/4257
echo $ Bazel testing...
if [ -z "${CI:-""}" ]; then
  # Skip (slow!) JavaDoc generation when not running on CI
  "$BZL" query //... | grep -v //java/dev/enola:javadoc | xargs "$BZL" test --test_env=ENOLA.DEV_AZKABAN="$HOME/.config/enola/azkaban.yaml" --explain ~/bazel-test-explain.txt --test_size_filters=small

else # On CI
  # Non-regression for problems like https://github.com/enola-dev/enola/issues/1146 and https://github.com/enola-dev/enola/issues/1164
  bazel mod graph --depth=1

  # See https://github.com/enola-dev/enola/issues/1116 why it's worth to re-PIN, on CI:
  REPIN=1 bazel run @maven//:pin

  # Runs git status and git diff to ensure no uncommitted changes
  tools/git/test.bash

  # Test all Bazel targets
  # https://github.com/enola-dev/enola/issues/1780
  "$BZL" query //... | xargs "$BZL" test --experimental_ui_max_stdouterr_bytes=-1
  cat ~/.bazel/execroot/_main/bazel-out/k8-fastbuild/testlogs/java/dev/enola/cli/tests/test.log || true

  # Run Enola CLI integration tests!
  ./test-cli.bash
fi

# The following makes sure that this test.bash will run as a pre-commit hook.
# NB: We DO NOT want to "pre-commit install" because that won't run Bazel!
# (And because our own venv etc. stuff above is better for the "first touch" contributor experience.)
# This is intentionally only done here at the END of successfully running the tests above,
# because only if we reach here we now that everything above actually works well locally.

echo
# Run https://pre-commit.com, see .pre-commit-config.yaml;
# locally run only on last commit (quick), but on CI
# <https://stackoverflow.com/a/75223617/421602>
# do run on all files, even if a bit slower.
# This prevents "cheating" and tech debt.
#
# PS: models/build.bash & ./tools/docs/build.bash must have run already... TODO automate dependency, with Bazel?
if [ -z "${CI:-""}" ]; then
  echo "$ pre-commit run (locally, only changed files)"
  pre-commit run
else
  echo "Skip running pre-commit --all-files on CI, it will run after tools/docs/build.bash Build Docs Site"
fi

tools/git/install-hooks.bash
