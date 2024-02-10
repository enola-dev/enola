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

# This script checks if https://pre-commit.com is available,
# and correctly installs it (into a Python Virtual Environment, if not.
# This script should be "sourced" from another script, not directly executed!

if ! [ -e ".venv/bin/pre-commit" ]; then
  echo "https://pre-commit.com is not available..."

  if ! [ -x "$(command -v python3)" ]; then
    echo "python3 is not installed, please run e.g. 'sudo apt-get install virtualenv python3-venv' (or an equivalent)"
    exit 255
  fi

  if ! [ -d .venv/ ]; then
    python3 -m venv .venv
  fi
  # shellcheck disable=SC1091
  source .venv/bin/activate

  .venv/bin/pip install -r requirements.txt
else
  # shellcheck disable=SC1091
  source .venv/bin/activate
fi
