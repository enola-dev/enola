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
    name = "enola",
    srcs = glob(["src/main/java/**/*.java"]),
    main_class = "dev.enola.cli.Enola",
    deps = [
        "//common/common",
        "//common/markdown",
        "//common/protobuf",
        "//core/impl",  # TODO Ideally shouldn't be required...
        "//core/lib:core_java_grpc",
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "//web/api",
        "//web/rest",
        "//web/sun",
        "//web/ui",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:info_picocli_picocli",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    jvm_flags = ["--enable-preview"],
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":enola",
        "//common/common",
        "//common/markdown",
        "//core/impl",
        "//web/rest",
        "//web/sun",
        "//web/ui",
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
