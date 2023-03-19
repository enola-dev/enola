<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

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

# Core

Enola 🕵🏾‍♀️ Core is a _library_ of [generic concepts of Enola's problem space](core-arch.md).

It is independent of "domains" such as Network, Linux, Kubernetes, Web, etc.

End-users use Enola through different "editions". Organization can build their own
internal editions of Enola, to interface with their proprietary in-house systems.

This Core's functionality which [is implemented](implementation.md) and exposed through different _Tools._
<!-- TODO ? The focus of the initial work is the `be` CLI tool, as illustrated by the [Kubernetes Edition](../k8s/index.md). -->
