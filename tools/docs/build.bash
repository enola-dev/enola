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

# This script assumes that ../../test.bash already ran once to set up things!

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
  rm -rf docs/dev/proto
  trap - EXIT
}
trap cleanup EXIT

rpl -R -x.md ../.. https://github.com/enola-dev/enola/blob/main docs/

bazelisk build //...
mkdir -p docs/dev/proto/
cp bazel-bin/core/proto/core_proto_doc/core_proto_doc.md docs/dev/proto/

find docs/use -maxdepth 1 -not -path docs/use -type d -exec tools/demo/build.bash {} \;

# TODO https://github.com/mkdocs/mkdocs/issues/1755
mkdocs build --strict --config-file mkdocs.yaml
cleanup

cd site/
xdg-open http://localhost:8000
python3 -m http.server
