#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2025 The Enola <https://enola.dev> Authors
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

# TODO And where does coursier come from?! ;-)

git branch | grep bazel-steward/ | xargs git branch -D

cs launch org.virtuslab:bazel-steward:1.6.0 --main org.virtuslab.bazelsteward.app.Main -- --update-all-prs

git add MODULE.bazel MODULE.bazel.lock maven_install.json
# TODO git commit -m "build(deps): Bump loads of stuff using Bazel Steward"
