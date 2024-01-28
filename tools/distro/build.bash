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

# TODO Replace this shell script with Bazel targets?

mkdir -p site/download/latest/
set -euox pipefail

# Build the end-user distributed executable fat über JAR
# NB: "bazelisk build //..." does *NOT* build "//cli:enola_deploy.jar", for some reason
# TODO Remove --nojava_header_compilation when https://github.com/bazelbuild/bazel/issues/21119 is fixed
bazelisk build --nojava_header_compilation //cli:enola_deploy.jar
cp tools/distro/execjar-header.bash site/download/latest/enola
cat bazel-bin/cli/enola_deploy.jar >>site/download/latest/enola

# Build the Container Image
# NB: This must work both on Docker (which turns it into docker buildx build) and Podman!
docker build -t localhost/enola:latest .
