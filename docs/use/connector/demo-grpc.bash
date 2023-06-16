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

DEMO="$(realpath "$(dirname "$0")")"
ROOT="$DEMO"/../../..

bazelisk build //connectors/demo:demo_deploy.jar 2>/dev/null

set -x

# NOT bazelisk run //connectors/demo 9090 &
"$ROOT"/bazel-bin/connectors/demo/demo --singlejar 9090 &

# TODO rm? while ! timeout 1 bash -c "echo > /dev/tcp/localhost/9090"; do sleep 1; done;

"$ROOT"/enola get --model file:"$DEMO"/model-grpc.yaml --format yaml demo.book_kind/0-13-140731-7

kill "$(cat /tmp/ServerPID)"
