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
    name = "jackson",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/object",
        "@enola_maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@enola_maven//:com_fasterxml_jackson_core_jackson_core",
        "@enola_maven//:com_fasterxml_jackson_core_jackson_databind",
        "@enola_maven//:com_fasterxml_jackson_dataformat_jackson_dataformat_yaml",
        "@enola_maven//:com_fasterxml_jackson_datatype_jackson_datatype_jsr310",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
        "@enola_maven//:org_yaml_snakeyaml",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    deps = [
        ":jackson",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/object",
        "//test",
        "@enola_maven//:org_jspecify_jspecify",
    ],
)
