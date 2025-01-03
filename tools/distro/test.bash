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

# TODO Transform this into a Bazel target instead...
set -euox pipefail

tools/distro/build.bash

# TODO Test output
site/download/latest/enola help
site/download/latest/enola --version
site/download/latest/enola get --load test/picasso.ttl http://example.enola.dev/Picasso

# TODO Test output
ENOLA_IMAGE=localhost/enola:latest docs/download/latest/enolac help
ENOLA_IMAGE=localhost/enola:latest docs/download/latest/enolac --version
ENOLA_IMAGE=localhost/enola:latest docs/download/latest/enolac \
  get --load test/picasso.ttl http://example.enola.dev/Picasso
