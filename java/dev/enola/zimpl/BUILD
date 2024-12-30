# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023 The Enola <https://enola.dev> Authors
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

load("@rules_java//java:defs.bzl", "java_binary", "java_test")

java_binary(
    name = "zimpl",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    main_class = "dev.enola.Main",
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola:api",
        "//java/dev/enola/common",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/data",
        "//java/dev/enola/model",
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)

# TODO Use load("//tools/bazel:junit.bzl", "junit_tests")
java_test(
    name = "EnolaTest",
    size = "small",
    srcs = ["EnolaTest.java"],
    runtime_deps = [
        "@enola_maven//:org_slf4j_slf4j_simple",
    ],
    deps = [
        ":zimpl",
        "//java/dev/enola:api",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//java/dev/enola/model",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:com_google_truth_extensions_truth_java8_extension",
        "@enola_maven//:com_google_truth_truth",
        "@enola_maven//:junit_junit",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)
