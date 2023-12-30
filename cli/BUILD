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
load("@rules_graalvm//graalvm:defs.bzl", "native_image")

java_library(
    name = "enola_library",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//common/common",
        "//common/markdown",
        "//common/protobuf",
        "//core/impl",
        "//core/lib:core_java_grpc",
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "//web/api",
        "//web/rest",
        "//web/sun",
        "//web/ui-soy",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:info_picocli_picocli",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)

java_binary(
    name = "enola",
    main_class = "dev.enola.cli.Enola",
    runtime_deps = [
        ":enola_library",
    ]
)

native_image(
    name = "enola-native",
    deps = [":enola_library"],
    main_class = "dev.enola.cli.Enola",
    native_image_tool = "@graalvm//:native-image",
)

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":enola_library",
        "//common/common",
        "//common/markdown",
        "//core/impl",
        "//web/rest",
        "//web/sun",
        "//web/ui-soy",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_truth_truth",
        "@maven//:info_picocli_picocli",
        "@maven//:io_github_classgraph_classgraph",
        "@maven//:junit_junit",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
) for name in glob([
    "src/test/java/**/*Test.java",
])]
