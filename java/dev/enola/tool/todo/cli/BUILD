# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

load("@rules_java//java:defs.bzl", "java_binary", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "todo",
    main_class = "dev.enola.tool.todo.cli.ToDoMain",
    runtime_deps = [":cli"],
)

java_library(
    name = "cli",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    plugins = ["//tools/bazel/java_plugin:picocli"],
    visibility = ["//visibility:public"],
    deps = [
        "//java/dev/enola/cli/common",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/object/jackson",
        "//java/dev/enola/tool/todo",
        "//java/dev/enola/tool/todo/config",
        "@maven//:com_google_guava_guava",
        "@maven//:info_picocli_picocli",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    deps = [
        ":cli",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/tool/todo",
    ],
)
