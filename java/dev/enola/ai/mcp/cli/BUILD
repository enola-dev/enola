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
load("@rules_java//java:java_binary.bzl", "java_binary")

java_binary(
    name = "mcp",
    main_class = "dev.enola.ai.mcp.cli.McpApplication",
    runtime_deps = [":cli"],
)

java_library(
    name = "cli",
    srcs = glob(
        ["*.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/ai/mcp",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/io/object/jackson",
        "//java/dev/enola/common/jackson",
        "//java/dev/enola/common/secret/auto",
        "@maven//:com_github_vorburger_java_sdk_mcp_core",
        "@maven//:com_github_vorburger_java_sdk_mcp_json_jackson2",
        "@maven//:com_google_guava_guava",
        "@maven//:info_picocli_picocli",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
