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
    name = "xml",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
            "Test*.java",
        ],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    resource_strip_prefix = "java/",
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_auto_service_auto_service_annotations",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob([
        "*Test.java",
    ]),
    deps = [
        ":xml",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/testlib",
        "//test",
    ],
)
