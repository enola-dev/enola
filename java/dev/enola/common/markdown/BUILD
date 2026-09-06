# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

load("@rules_java//java:java_library.bzl", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "markdown",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    resource_strip_prefix = "java/dev/enola/common/markdown/resources/",
    resources = glob(["resources/**/*"]),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/jackson",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:org_commonmark_commonmark",
        "@maven//:org_commonmark_commonmark_ext_autolink",
        "@maven//:org_commonmark_commonmark_ext_gfm_tables",
        "@maven//:org_commonmark_commonmark_ext_heading_anchor",
        "@maven//:org_commonmark_commonmark_ext_task_list_items",
        "@maven//:org_commonmark_commonmark_ext_yaml_front_matter",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":markdown",
        "//java/dev/enola/common/diff/testlib",
        "//java/dev/enola/common/io",
        "@maven//:com_google_guava_guava",
        "@maven//:org_commonmark_commonmark",
        "@maven//:org_commonmark_commonmark_ext_yaml_front_matter",
    ],
)
