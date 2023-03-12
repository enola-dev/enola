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

if ! [ -d ./.venv/ ]; then
  python3 -m venv .venv
fi
# shellcheck disable=SC1091
source .venv/bin/activate

if ! [ -e .venv/lib64/python3.11/site-packages/mkdocs ]; then
  pip install mkdocs-material==9.1.2 \
              mkdocs-git-revision-date-localized-plugin==1.2.0 \
              mkdocs-git-committers-plugin-2==1.1.1
fi

if ! git update-index --refresh >/dev/null; then
  git status
  echo "Build only works if there are no git Changes not staged for commit! Abort."
  exit 255
fi

DIR=$(pwd)
cleanup() {
  cd "$DIR"
  git restore docs/
  rm -rf docs/proto
}
trap cleanup EXIT

# TODO Replace https://github.com/vorburger/enola/blob/private with https://github.com/enola-dev/enola/blob/main
rpl -R -x.md ../.. https://github.com/vorburger/enola/blob/private docs/

bazelisk build //...
mkdir -p docs/proto/
cp bazel-bin/core/proto/core_proto_doc/core_proto_doc.md docs/proto/

mkdocs build --strict --config-file mkdocs.yaml
mkdocs serve --strict --config-file mkdocs.yaml
