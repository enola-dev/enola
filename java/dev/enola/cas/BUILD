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

load("@rules_java//java:defs.bzl", "java_binary")
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "cas",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/io",
        "@enola_maven//:com_github_ipld_java_cid",
        "@enola_maven//:com_github_multiformats_java_multibase",
        "@enola_maven//:com_github_multiformats_java_multihash",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    size = "medium",
    srcs = glob(
        ["*Test.java"],
        exclude = [],
    ),
    srcs_utils = [
    ],
    deps = [
        ":cas",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "@enola_maven//:com_github_ipld_java_cid",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)
