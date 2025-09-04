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

load("@rules_java//java:defs.bzl", "java_binary", "java_library")

java_binary(
    name = "hello",
    main_class = "dev.enola.common.hello.Hello",
    runtime_deps = [":hello_lib1"],
)

java_library(
    name = "hello_lib1",
    srcs = ["src/main/java/dev/enola/common/hello/Hello.java"],
    runtime_deps = [
        "@maven//:org_apache_logging_log4j_log4j_to_jul",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [":hello_lib2"],
)

java_library(
    name = "hello_lib2",
    srcs = ["src/main/java/dev/enola/common/hello/Library.java"],
    deps = ["@maven//:org_slf4j_slf4j_api"],
)
