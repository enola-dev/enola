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

# Inspired by https://gist.github.com/spullara/782523
# BEWARE of https://www.shellcheck.net/wiki/SC3050 (that's why Bash instead of /bin/sh)
commandToRun="$(printf "%q " "$@")"
if test "$commandToRun" = "'' "; then
  eval "exec java -Xmx1G --enable-preview -jar $0"
else
  eval "exec java -Xmx1G --enable-preview -jar $0 $commandToRun"
fi
