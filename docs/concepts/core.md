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

Enola at its core can be viewed as a
[Knowledge Management tool](https://en.m.wikipedia.org/wiki/Knowledge_management)
to describe an [Ontology](https://en.m.wikipedia.org/wiki/Ontology_(information_science)).
It _models_ real world concepts as what it terms _Entities,_ [identified by URI](uri.md), and models their _references_ both
across its Entities, as well as to arbitrary non-Enola URIs. (This notably includes traditional
URLs like HTTP links, which models can use to create hyperlinks to UIs of applications managing Entities.)

Enola has [built-in interchangeable support](../use/rosetta/index.md) for JSON, YAML, and Text proto
[wire formats](https://en.m.wikipedia.org/wiki/Comparison_of_data-serialization_formats) for entities.
<!-- TODO In the future, maybe (binary!) PB? FHIR? Blockchain EVM format? -->

Enola currently uses [Proto 3](https://protobuf.dev/programming-guides/proto3/) as its
Schema language, but is conceptually open to supporting other kinds of schemas in the future; perhaps e.g.
[JSON Schema](https://github.com/enola-dev/enola/issues/313), or XSD XML Schema, or
[ASN.1](https://en.m.wikipedia.org/wiki/ASN.1) or [GNU poke](https://www.gnu.org/software/poke/).
