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
    main_class = "dev.enola.common.exec.pty.Demo",
    runtime_deps = [
        ":pty",
        "@maven//:org_slf4j_slf4j_simple",
    ],
)

java_library(
    name = "pty",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
            "TestLoggerRule.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/exec",
        "//java/dev/enola/common/io/util",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:org_jetbrains_pty4j_pty4j",
        "@maven//:org_jline_jline",  # for Terminal WINCH size & WriterOutputStream
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        include = ["*Test.java"],
        allow_empty = True,
    ),
    srcs_utils = ["TestLoggerRule.java"],
    visibility = ["//:__subpackages__"],
    deps = [
        ":pty",
        "//java/dev/enola/common/concurrent",
        "@maven//:org_jline_jline",  # for Terminal WINCH size
        "@maven//:org_slf4j_slf4j_api",
    ],
)
