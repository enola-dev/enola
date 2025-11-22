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

load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "common",
    srcs = glob(["*.java"]),
    visibility = [
        # TODO Change "//java/dev/enola:__pkg__" visibility to tools/javadoc/all
        "//java/dev/enola:__pkg__",
        "//java/dev/enola/ai/mcp/cli:__pkg__",
        "//java/dev/enola/cli:__subpackages__",
    ],
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/locale",
        "//java/dev/enola/common/logging",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/time",
        "@maven//:com_google_guava_guava",
        "@maven//:info_picocli_picocli",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
