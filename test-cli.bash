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

set -euox pipefail

# This script tests Enola CLI invocations.
# See also EnolaCLITest

# Integration test the MCP related stuff; initially added to catch https://github.com/enola-dev/enola/issues/1862 regressions:
./enola -vv mcp call-tool modelcontextprotocol/everything echo '{"message":"hi"}'
# TODO The ai sub-command currently doesn't actually return 1 instead of 0 on errors, so this is not really not testable yet, but still useful:
./enola -vv ai --agents=test/agents/everything.agent.yaml --prompt "Print environment variables to debug MCP"
# TODO GitHub Token on CI? LLM?? ./enola ai --agents=test/agents/github.agent.yaml --prompt "How many stars do the top 3 repos that I own on GitHub repo have? (Use the GitHub context tool to find by GitHub user name.)"
./enola -vv mcp list-tools

# PS: Update tika.md with anything (of interest) added here
