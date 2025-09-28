#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

# Build the end-user distributed executable fat über JAR

THIS="$(dirname "$(realpath "$0")")"
ROOT="$THIS"/../..

mkdir -p "$ROOT"/site/download/latest/
set -euox pipefail

if [ -x "$(command -v gbazelisk)" ]; then
  BZL=gbazelisk
elif [ -x "$(command -v bazel)" ]; then
  BZL=bazel
elif [ -x "$(command -v bazelisk)" ]; then
  BZL=bazelisk
else
  # Also in test.bash
  echo "bazelisk is not installed, please run e.g. 'go install github.com/bazelbuild/bazelisk@latest' "
  echo "or an equivalent from https://github.com/bazelbuild/bazelisk#installation or see docs/dev/setup.md"
  exit 255
fi

"$ROOT"/tools/protoc/protoc.bash

# NB: "bazelisk build //..." does *NOT* build *_deploy.jar, for some reason
"$BZL" build --color=yes //java/dev/enola/cli:enola_deploy.jar
rm -f "$ROOT"/site/download/latest/enola.jar
ln "$ROOT"/bazel-bin/java/dev/enola/cli/enola_deploy.jar "$ROOT"/site/download/latest/enola.jar
