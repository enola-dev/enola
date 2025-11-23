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

load("@rules_java//java:defs.bzl", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "testlib",
    testonly = True,
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/testlib",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_truth_truth",
        "@maven//:org_eclipse_rdf4j_rdf4j_model",
        "@maven//:org_eclipse_rdf4j_rdf4j_model_api",
        "@maven//:org_jspecify_jspecify",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    deps = [
        ":testlib",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//models",
        "//test",
        "@maven//:org_jspecify_jspecify",
    ],
)
