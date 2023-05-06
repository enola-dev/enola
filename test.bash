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

# Same also in the ./enola script:
if ! [ -x "$(command -v bazelisk)" ]; then
    echo "bazelisk is not installed, please run e.g. 'go install github.com/bazelbuild/bazelisk@latest' "
    echo "or an equivalent from https://github.com/bazelbuild/bazelisk#installation or see docs/dev/setup.md"
    exit 255
fi

# https://github.com/bazelbuild/bazel/issues/4257
# https://bazel.build/reference/command-line-reference#flag--build_tests_only
echo $ b test //...
bazelisk test --nobuild_tests_only //...
echo
echo $ b build //...
bazelisk build //...

echo
echo $ tools/demo/run.bash
tools/demo/run.bash

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

# We need to pip install "every time" (and not just e.g. inside the if above),
# because otherwise changes to requirements.txt don't lead to updates locally and on CI.
# TODO Is there a way to only run pip install "if requirements.txt changed"? With Bazel?!
echo
echo $ pip install -r requirements.txt
pip install -r requirements.txt

echo
echo $ pre-commit run
pre-commit run

# This makes sure that this test.bash will run as a pre-commit hook
# NB: We DO NOT want to "pre-commit install" because that won't run bazelisk!
# (And because our own venv etc. stuff above is better for the "first touch" contributor experience.)
# This is intentionally only done here at the END of successfully running the tests above,
# because only if we reach here we now that everything above actually works well locally.
tools/git/install-hooks.bash
