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

load("@rules_graalvm//graalvm:defs.bzl", "native_image")
load("@rules_java//java:defs.bzl", "java_binary", "java_library")

java_library(
    name = "demolib",
    srcs = glob(["*.java"]),
    plugins = ["//tools/bazel/java_plugin:picocli"],
    deps = [
        "//java/dev/enola/cli/common",
        "@enola_maven//:info_picocli_picocli",
    ],
)

java_binary(
    name = "demo",
    main_class = "dev.enola.cli.demo.DemoApplication",
    # TODO Change "//java/dev/enola:__pkg__" visibility to tools/javadoc/all
    visibility = ["//java/dev/enola:__pkg__"],
    runtime_deps = [
        ":demolib",
    ],
)

native_image(
    name = "demo-native",
    main_class = "dev.enola.cli.demo.DemoApplication",
    native_image_tool = "@graalvm//:native-image",
    deps = [":demolib"],
)
