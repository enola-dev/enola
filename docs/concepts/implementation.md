<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

# Implementation

## Architecture

This project is _"polyglot"_ (cool with using multiple programming languages).

Connectors written in https://www.emojicode.org are actively being worked on.

We follow a _"[UNIX philosophy](https://en.wikipedia.org/wiki/Unix_philosophy)"_-inspired approach to modularity among the subsystems and within their code.

We do not think it's necessarily all that bad to "shell out" (exec) to invoke existing CLI tools,
if this can significantly accelerate required integrations, or simplify authentication & authorization security.
But such tools such produce output in some machine readable structured text format (such as JSON, YAML, TextProto)
or even a well-known binary format (such as Protocol Buffers binary serialization), not formatted text output intended for humans.

## Java

The initial implementation of [the Core](core.md) is in Java.

The only reason the initial author of the project chose Java
was that this allowed him to be most productive, because of his prior
knowledge in this particular programming language's ecosystem.

The fact that the core was initially written in Java is very much
considered an "implementation detail" which should not "leak" to
end-users of [the core library](core.md) and its packaging in Editions,
such as for [Kubernetes](../k8s/index.md), and its API clients.

GraalVM native image builds are one way to hide Java installation etc.
