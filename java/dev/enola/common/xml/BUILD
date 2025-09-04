# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2024 The Enola <https://enola.dev> Authors
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
    name = "xml",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:org_jspecify_jspecify",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    deps = [
        ":xml",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//test",
    ],
)
