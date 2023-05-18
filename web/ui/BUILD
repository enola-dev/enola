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

load("@rules_java//java:defs.bzl", "java_library", "java_test")

java_library(
    name = "ui",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    visibility = [
        "//cli:__subpackages__",
    ],
    deps = [
        "//common/common",
        "//core/impl",  # TODO Move class Soy, avoid full impl dep
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "//web/api",
        "@maven//:com_google_guava_guava",
    ],
)

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":ui",
        "//common/common",
        "//common/protobuf",
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "//web/api",
        "//web/sun",
        "@maven//:com_google_truth_truth",
    ],
) for name in glob([
    "src/test/java/**/*Test.java",
])]
