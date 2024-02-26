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

DIR=$(pwd)
# https://unix.stackexchange.com/a/476710/103272
OLDOPTS="$(set +o); set -${-//c}"
trap cleanup EXIT
set +euo pipefail
function cleanup {
  eval "${OLDOPTS}"
  cd "$DIR" || exit 8
  trap - EXIT
}

# This obtains the path to the JDK (its "Java toolchain") which Bazel downloaded.
#
# For background, see:
#   * https://github.com/enola-dev/enola/issues/546
#   * https://github.com/salesforce/bazel-eclipse/blob/888bcd333ac7bd4166fdb411562b74c2b54514d5/bundles/com.salesforce.bazel.eclipse.core/src/com/salesforce/bazel/eclipse/core/model/discovery/BaseProvisioningStrategy.java#L945-L960
#   * https://stackoverflow.com/questions/78057833/how-to-query-bazel-for-the-absolute-jave-home-like-path-to-the-remote-jdk-of

function java_binary {
  local current_java_runtime

  ROOT="$(realpath "$(dirname "${BASH_SOURCE[0]}"/)")"
  cd "$ROOT" || exit 9

  # Hide Bazel output, unlike it failed (same also in //enola)
  LOG=$(mktemp)
  if ! current_java_runtime="$(bazelisk cquery @bazel_tools//tools/jdk:current_java_runtime \
    --output starlark --starlark:file "$ROOT"/bazel-java-tool-chain.bzl 2>"$LOG")"; then
    cat "$LOG"
    rm "$LOG"
    exit 7
  else
    rm "$LOG"
  fi

  LOG=$(mktemp)
  if ! output_base="$(bazel info output_base 2>"$LOG")"; then
    cat "$LOG"
    rm "$LOG"
    exit 7
  else
    rm "$LOG"
  fi

  echo "$output_base/$current_java_runtime/bin/java"
}
