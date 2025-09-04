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
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "demo",
    main_class = "dev.enola.ai.adk.web.QuickstartDemoWebMain",
    runtime_deps = [
        ":web",
        "@maven//:org_slf4j_slf4j_simple",
    ],
)

java_library(
    name = "web",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/ai/adk/core",
        "//java/dev/enola/ai/adk/test",
        "//java/dev/enola/common/rx",
        # "@maven//:com_google_adk_google_adk",
        "@maven//:com_github_vorburger_adk_java_google_adk",
        # "@maven//:com_google_adk_google_adk_dev",
        "@maven//:com_github_vorburger_adk_java_google_adk_dev",
        "@maven//:com_google_guava_guava",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_boot_spring_boot_autoconfigure",
        "@maven//:org_springframework_spring_beans",
        "@maven//:org_springframework_spring_context",
        "@maven//:org_springframework_spring_core",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":web",
        "//java/dev/enola/ai/adk/core",
        "//java/dev/enola/ai/adk/test",
        "//java/dev/enola/common/io",
        # "@maven//:com_google_adk_google_adk",
        "@maven//:com_github_vorburger_adk_java_google_adk",
    ],
)
