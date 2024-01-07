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

# Same also in the ./enola script:
if ! [ -x "$(command -v bazelisk)" ]; then
    echo "bazelisk is not installed, please run e.g. 'go install github.com/bazelbuild/bazelisk@latest' "
    echo "or an equivalent from https://github.com/bazelbuild/bazelisk#installation or see docs/dev/setup.md"
    exit 255
fi

# https://github.com/bazelbuild/bazel/issues/4257
echo $ Bazel testing...
bazelisk query //... | xargs bazel test

# Test distros: 1. End-user distributed executable fat über JAR, 2. Container Image
tools/distro/test.bash

# Check if https://pre-commit.com is available (and try to install it not)
if ! [ -e "./.venv/bin/pre-commit" ]; then
  echo "https://pre-commit.com is not available..."

  if ! [ -x "$(command -v python3)" ]; then
    echo "python3 is not installed, please run e.g. 'sudo apt-get install virtualenv python3-venv' (or an equivalent)"
    exit 255
  fi

  if ! [ -d ./.venv/ ]; then
    python3 -m venv .venv
  fi
  # shellcheck disable=SC1091
  source ./.venv/bin/activate
else
  # shellcheck disable=SC1091
  source ./.venv/bin/activate
fi

# pip install - but only if required! ;)
tools/be/pip-installed.bash

echo
# Run https://pre-commit.com, see .pre-commit-config.yaml;
# locally run only on last commit (quick), but on CI
# <https://stackoverflow.com/a/75223617/421602>
# do run on all files, even if a bit slower.
# This prevents "cheating" and tech debt.
set +u
if [ -z "$CI" ]; then
  echo "$ pre-commit run (locally, only changed files)"
  pre-commit run
else
  echo "$ pre-commit run --all-files (on CI)"
  pre-commit run --all-files
fi
set -u

# This makes sure that this test.bash will run as a pre-commit hook
# NB: We DO NOT want to "pre-commit install" because that won't run bazelisk!
# (And because our own venv etc. stuff above is better for the "first touch" contributor experience.)
# This is intentionally only done here at the END of successfully running the tests above,
# because only if we reach here we now that everything above actually works well locally.
tools/git/install-hooks.bash
