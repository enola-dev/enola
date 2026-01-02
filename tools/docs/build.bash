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

# This script assumes that ../../test.bash already ran, to set up tools, and build!

tools/git/test.bash

DIR=$(pwd)
cleanup() {
  cd "$DIR"
  git restore docs/
  trap - EXIT
}
trap cleanup EXIT

# Copy Third Party JS & CSS into docs/BUILT/THIRD_PARTY
rm -rf docs/BUILT/third_party
mkdir docs/BUILT/third_party
find third_party/web/ -type d -exec mkdir -p docs/BUILT/{} \;
find third_party/web/ -type f -exec ln -f {} docs/BUILT/{} \;
rm -f docs/BUILT/third_party/{BUILD*,*.bash}

# Fix up //... links in docs/**/*.md link to GitHub (instead of docs.enola.dev)
find docs/ -type f -name "*.md" -print0 \
  | xargs -n 1 -0 sed -i 's|(//|(https://github.com/enola-dev/enola/blob/main/|g'

# We just always install https://github.com/marionebl/svg-term-cli, even if
# we're then skipping it below, because this is quick, and doing it anyway helps
# to detecting any CI build system regression with Nix etc.
if ! [ -f "node_modules/.bin/svg-term" ]; then
  npm ci
fi
PATH="$(pwd)/node_modules/.bin:$PATH"
export PATH
svg-term --version

# This is very slow (so the svg-term-cli install is before; but its use has to be after this)
# Keep 'docs/use/**/*.md' in sync with tools/demo/test.bash & below
./enola execmd -vvvi "docs/use/**/*.md"

# Skip (lengthy!) demo screencasts build if this script is called with any argument (handy during dev)
if [ $# -eq 0 ]; then
  # shellcheck disable=SC2016
  # TODO Replace this with docs/use/**/BUILD files, so that demo tests only run if inputs change!
  find docs/use -maxdepth 1 -not -path docs/use -type d -print0 | \
    xargs -n 1 -0 sh -c 'tools/demo/build.bash $0 || exit 255'
fi

# models/build.bash could be run at this point, but we (have to) ran it in test-ci/test.bash already.

TOOLS_DIR=$(realpath "$(dirname "$0")")
ENOLA="$TOOLS_DIR"/../../enola
"$ENOLA" -vvvvvvv execmd -i docs/models/example.org/*.md

# TODO https://github.com/mkdocs/mkdocs/issues/1755
mkdocs build --strict --config-file mkdocs.yaml

cleanup

# JavaDoc
rm -rf site/dev/javadoc/
unzip .built/javadoc.jar -d site/dev/javadoc/
# xdg-open site/dev/javadoc/index.html

# Enola Binary Distro
tools/distro/build.bash

# https://docs.enola.dev/demo/
web/distro

# https://docs.enola.dev/maven-repo/
mkdir -p site/maven-repo/dev/enola/
cp -r ~/.m2/repository/dev/enola/ site/maven-repo/dev/
