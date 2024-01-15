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

# This runs "pip install", but (only) if requirements.txt has changed.

# TODO Cute idea, but this doesn't actually work, like this... fix!

# TODO Use sha512sum (or b2sum) instead of md5sum!
# TODO Use cksum <https://man7.org/linux/man-pages/man1/cksum.1.html> instead, with parameter?
# TODO Replace this with Enola's very own Resource hash() and exec() ...

HASH_FILE=.pip-install.hash.txt

# Always run on CI (because THIS IS BROKEN!)
if ! md5sum --warn --status --check $HASH_FILE || [ -n "$CI" ]; then
  echo
  set -e
  pip install -r requirements.txt
  md5sum --tag requirements.txt >$HASH_FILE
  # Technically not required, but let's just double check:
  md5sum --warn --status --strict --check $HASH_FILE
else
  echo "Skipping running pip install - BUT THIS DETECTION IS BROKEN ANYWAY AS-IS! :("
fi

# PS: A "real" (non-script) implementation should hash the command itself as well.