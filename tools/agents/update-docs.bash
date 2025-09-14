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

set -euox pipefail

# TODO Automate finding all ../*-agent instead of hard-coding each name here

cp ../git-commit-message-agent/README.md docs/agents/git-commit.md
cp ../github-issue-agent/README.md docs/agents/github-issue.md

MSG="\n<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->"
echo -e "$MSG" >>docs/agents/git-commit.md
echo -e "$MSG" >>docs/agents/github-issue.md
