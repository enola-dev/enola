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

# This script sets environment variables which are useful for Enola developpers.
# Note that the Bazel build tool does not required these. The goal of this is
# however to give developpers the same environment in their respective shell.
# This also helps to launch IDEs, such as VSC, and make them use the same
# (versions of the) tools which run under Bazel for building & testing.

# As explained on https://docs.enola.dev/dev/setup/,
# Developers must run this script via ". .envrc" (NOT ./.envrc).
# Other scripts do so likewise (via source, same as ".").
# Ergo, no +x no this one.

# Set-Up Go
# Go is required to install Bazelisk (the way we install it), by Bazel,
# due to go_sdk.host() in MODULE.bazel, for Go development in Enola.
if ! [ -x "$(command -v go)" ]; then
  echo "Please install Go from https://go.dev/doc/install and re-run this script!"
  echo "See also https://docs.enola.dev/dev/setup/"
  exit 255
fi

if [ -x "$(command -v gbazelisk)" ]; then
  BZL=gbazelisk
elif [ -x "$(command -v bazelisk)" ]; then
  BZL=bazelisk
else
  source tools/go/install.bash
fi

# Set-Up Java
# This makes us use the exact same JDK as used by Bazel.
# (Which is the toolchains:remotejdk_21 in tools/java_toolchain/BUILD.)
source "$ROOT"/tools/bazel-java-tool-chain/bazel-java-tool-chain.bash
bazel_java_home=$(bazel_java_home)
export bazel_java_home

# This breaks ./enola, see https://github.com/enola-dev/enola/issues/574
#JAVA_HOME=$bazel_java_home
#export JAVA_HOME
#PATH=$bazel_java_home/bin:$PATH
#export PATH

# TODO Write a setup.fish script... can it invoke this one?
