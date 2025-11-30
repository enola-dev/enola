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

load("@rules_java//java:defs.bzl", "java_binary", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "websocket",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    deps = [
        "//java/dev/enola/audio/voice/twilio/relay",
        "//java/dev/enola/audio/voice/twilio/security",
        "//java/dev/enola/common",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/logging",
        "//java/dev/enola/common/net/websocket",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/auto",
        "@maven//:com_google_guava_guava",
        "@maven//:org_java_websocket_Java_WebSocket",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_binary(
    name = "echo",
    main_class = "dev.enola.audio.voice.twilio.relay.websocket.EchoConversationMain",
    runtime_deps = [":websocket"],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":websocket",
        "//java/dev/enola/audio/voice/twilio/relay",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/jackson/testlib",
        "//java/dev/enola/common/net/websocket",
        "//java/dev/enola/common/secret/auto",
        "@maven//:org_java_websocket_Java_WebSocket",
    ],
)
