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
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "tool",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    runtime_deps = [
        # Google GenAI uses Apache HttpComponents which uses Apache Commons Logging, which this bridges into SLF4j!
        "@maven//:org_slf4j_jcl_over_slf4j",
    ],
    deps = [
        "//java/dev/enola/common/jackson",
        "//java/dev/enola/ai/mcp",
        "//java/dev/enola/common",
        "//java/dev/enola/common/name",
        # "@maven//:com_google_adk_google_adk",
        "@maven//:com_github_vorburger_adk_java_google_adk",
        "@maven//:com_github_vorburger_java_sdk_mcp_core",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:io_reactivex_rxjava3_rxjava",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":tool",
        "//java/dev/enola/ai/adk/iri",
        "//java/dev/enola/ai/adk/test",
        "//java/dev/enola/ai/iri",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/auto",
        # "@maven//:com_google_adk_google_adk",
        "@maven//:com_github_vorburger_adk_java_google_adk",
        "@maven//:io_reactivex_rxjava3_rxjava",
    ],
)
