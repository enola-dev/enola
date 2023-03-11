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
DIR=$(realpath "$(dirname "$0")")

# shellcheck disable=SC1091
source "$DIR/../.venv/bin/activate"

if ! [ -e "$DIR/../.venv/lib64/python3.11/site-packages/mkdocs" ]; then
  pip install mkdocs-material==9.1.2 \
              mkdocs-git-revision-date-localized-plugin==1.2.0 \
              mkdocs-git-committers-plugin-2==1.1.1
fi

mkdocs build -f "$DIR/../mkdocs.yaml"
cd "$DIR/../site/"
xdg-open http://localhost:8000
python3 -m http.server
