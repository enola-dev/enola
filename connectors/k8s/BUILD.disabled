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

load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "enola-k8s",
    srcs = glob(["src/main/java/**/*.java"]),
    main_class = "dev.enola.k8s.Bek",
    deps = [
        "@maven//:io_kubernetes_client_java",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_kubernetes_client_java_proto",
    ],
)
