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

# This script assumes that ../../test.bash already ran, to set up tools, and build!

# shellcheck disable=SC1091
source .venv/bin/activate

if ! git update-index --refresh >/dev/null; then
  git status
  echo "Build only works if there are no git Changes not staged for commit! Abort."
  exit 255
fi

DIR=$(pwd)
cleanup() {
  cd "$DIR"
  git restore docs/
  trap - EXIT
}
trap cleanup EXIT

# Fix up //... links in docs/**/*.md link to GitHub (instead of docs.enola.dev)
find docs/ -type f -name "*.md" -print0 \
  | xargs -n 1 -0 sed -i 's|(//|(https://github.com/enola-dev/enola/blob/main/|g'

cp bazel-bin/common/thing/thing_proto_doc/thing_proto_doc.md docs/dev/proto/thing.md
cp bazel-bin/core/lib/core_proto_doc/core_proto_doc.md docs/dev/proto/core.md

# Skip (lengthy!) demo screencasts build if this script is called with any argument (handy during dev)
if [ $# -eq 0 ]; then
  if ! [ -x "$(command -v svg-term)" ]; then
    npm install -g svg-term-cli
  fi

  # shellcheck disable=SC2016
  # TODO Replace this with docs/use/**/BUILD files, so that demo tests only run if inputs change!
  find docs/use -maxdepth 1 -not -path docs/use -type d -print0 | \
    xargs -n 1 -0 sh -c 'tools/demo/build.bash $0 || exit 255'
fi

# models/build.bash could be run at this point, but during we (have to) ran it in test-ci/test.bash already.

TOOLS_DIR=$(realpath "$(dirname "$0")")
ENOLA="$TOOLS_DIR"/../../enola
"$ENOLA" -vvvvvvv execmd -i docs/models/example.org/*.md

# TODO https://github.com/mkdocs/mkdocs/issues/1755
mkdocs build --strict --config-file mkdocs.yaml

cleanup

# Enola Binary Distro
tools/distro/build.bash
