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
    main_class = "dev.enola.chat.Demo",
    runtime_deps = [
        ":chat",
        "@enola_maven//:org_slf4j_slf4j_simple",
    ],
)

java_library(
    name = "chat",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/ai/langchain4j",
        "//java/dev/enola/common",
        "//java/dev/enola/common/context",
        "//java/dev/enola/data/id",
        "//java/dev/enola/identity",
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:dev_langchain4j_langchain4j_core",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":chat",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/identity",
        "//java/dev/enola/thing:thing_java",
    ],
)
