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
load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_proto_grpc//java:defs.bzl", "java_grpc_library", "java_proto_library")

proto_library(
    name = "view_proto",
    srcs = ["src/main/java/dev/enola/web/enola_view.proto"],
    deps = [
        "@com_google_protobuf//:any_proto",
        "@com_google_protobuf//:struct_proto",
        "@com_google_protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "view_java_proto",
    protos = [
        "view_proto",
    ],
    visibility = [
        "//cli:__subpackages__",
        "//web/ui:__pkg__",
    ],
)

# java_library(
#     name = "api",
#     srcs = glob(["src/main/java/**/*.java"]),
#     visibility = [
#         "//web:__subpackages__",
#     ],
#     deps = [
#         "//common/common",
#         "@maven//:com_google_guava_guava",
#     ],
# )
