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

set -euo pipefail

# This script prepares the Development Environment container.

# @DEPRECATED Replace all callers with direct tools/flox invocations?

tools/flox

# TODO After https://github.com/enola-dev/enola/pull/452, uncomment:
# $(go env GOPATH)/bin/bazelisk run //tools/hello

# TODO tools/git/install-hooks.bash
