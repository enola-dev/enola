#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

MSG="\n<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->"

for agent_dir in ../*-agent; do
    agent_name=$(basename "$agent_dir")
    # NB: The destination file name is derived from the agent directory name.
    # E.g. ../github-issue-agent -> github-issue.md
    # E.g. ../git-commit-message-agent -> git-commit.md
    dest_name_base=${agent_name%-agent}
    dest_name=${dest_name_base%-message}

    dest_file="docs/agents/$dest_name.md"
    cp "$agent_dir/README.md" "$dest_file"
    echo -e "$MSG" >> "$dest_file"
done
