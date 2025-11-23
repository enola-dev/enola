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

# This script detects any usages of java.net.URL in **/*.java.
# See class dev.enola.common.io.iri.URL for background about why.
#
# It technically misses "implicit" usages of java.net.URL, such as:
# Resources.getResource("test.txt").toURI(); but that's OK, for now.
# TODO Write a better JVM level test; e.g. with ErrorProne or SpotBugs.

allow_list=("java/dev/enola/common/io/resource/UrlResource.java"
            "java/dev/enola/common/io/resource/ClasspathResource.java")

# TODO Also grep for .toURL() invocations, and fail for any (new) ones.

found_files=$(find . -name "*.java" -print0 | grep -Zzv VENDOR/ | xargs -0 grep -lE "(^|[^a-zA-Z0-9_.])java\.net\.URL($|[^a-zA-Z0-9_#}])" | while IFS= read -r file; do
    file_name=$(basename "$file")
    allow_path="${file//.\//}"

    allowed=false
    for allowed_file in "${allow_list[@]}"; do
        if [[ "$file_name" == "$allowed_file" ]] || [[ "$allow_path" == "$allowed_file" ]]; then
            allowed=true
            break
        fi
    done

    if "$allowed"; then
        continue
    fi

    echo "$file"
done)

if [[ -n "$found_files" ]]; then
    echo "Found use of forbidden bad java.net.URL in the following files, please fix:"
    echo "$found_files"
    exit 1
else
    exit 0
fi
