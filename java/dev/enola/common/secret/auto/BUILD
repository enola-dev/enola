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
    main_class = "dev.enola.common.secret.auto.AutoSecretManager",
    runtime_deps = [
        ":auto",
        "@enola_maven//:org_slf4j_slf4j_simple",
    ],
)

java_library(
    name = "auto",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/exec",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/exec",
        "//java/dev/enola/common/secret/yaml",
        "@enola_maven//:ch_vorburger_exec_exec",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(include = ["*Test.java"]),
    deps = [
        ":auto",
        "//java/dev/enola/common/secret",
    ],
)
