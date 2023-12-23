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

# See docs/dev/dependencies.md

# TODO How-to automagically update dependencies.txt if PyPI has newer versions? (pip-tools & requirements.in?)

# Update .pre-commit-config.yaml
# shellcheck disable=SC1091
source ./.venv/bin/activate
pre-commit autoupdate
pre-commit clean
pre-commit gc
pre-commit run --all-files

# Update Bazel's Maven dependencies MODULE.bazel (and maven_install.json)
# https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#outdated-artifacts
# Note that this doesn't actually update, just prints if *YOU* (manually) can.
bazelisk run @maven//:outdated
echo "PLEASE READ ^^^ TO SEE IF YOU CAN UPDATE ANYTHING IN MODULE.bazel?"
echo "Don't forget to run 'bazelisk run @unpinned_maven//:pin' after changes!"
