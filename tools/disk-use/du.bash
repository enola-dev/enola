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

# NB: Do NOT use -e nor -o pipefail, as 'head' returns signal 13
set -ux

# NB: The /* glob does not match hidden files and directories by default, so:
shopt -s dotglob

echo "https://github.com/enola-dev/enola/issues/1959"

df -h

# NB: du -a includes large files within directories, not just the directory sizes
sudo du -ash /* 2>/dev/null | sort -hr | head -n 10
du -ah | sort -rh | head -n 17
