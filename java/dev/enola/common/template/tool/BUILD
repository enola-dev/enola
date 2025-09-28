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
    name = "temply",
    main_class = "dev.enola.common.template.tool.TemplyMain",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":tool",
    ],
)

java_library(
    name = "tool",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/io/object",
        "//java/dev/enola/common/io/object/jackson",
        "//java/dev/enola/common/io/object/template",
        "//java/dev/enola/common/template",
        "//java/dev/enola/common/template/handlebars",
        "@maven//:com_google_guava_guava",
        "@maven//:org_jspecify_jspecify",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":tool",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/object/jackson",
        "//java/dev/enola/common/template",
        "//java/dev/enola/common/template/handlebars",
    ],
)
