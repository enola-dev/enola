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
It _models_ real world concepts as what it terms _Entities,_ [identified by URI](uri.md), and models their
_references_ both across its Entities, and to arbitrary non-Enola URIs. (This notably includes traditional
URLs like HTTP links, which models can use to create hyperlinks to UIs of applications managing Entities.)

## Formats

Enola currently has [built-in interchangeable support](../use/rosetta/index.md) for [RDF 🐢 Turtle](turtle.md), JSON, YAML, [XML](xml.md), and Text proto & Binary Protocol Buffers
[wire formats](https://en.m.wikipedia.org/wiki/Comparison_of_data-serialization_formats) for Things. It is conceptually open to supporting other formats in the future;
likely [RDF4j's Binary Format](https://rdf4j.org/documentation/reference/rdf4j-binary/), maybe also e.g. [CBOR](https://github.com/enola-dev/enola/issues/603)
(perhaps [RDF CBOR](https://openengiadina.codeberg.page/rdf-cbor/)?), or [IPFS IPLD Codecs](https://github.com/enola-dev/enola/issues/777)
or something like [Amazon Ion](https://amazon-ion.github.io/ion-docs/) or [others](https://en.m.wikipedia.org/wiki/Comparison_of_data-serialization_formats).
<!-- TODO Maybe also ... FHIR? Blockchain EVM format? What else? -->

## Schemas

Enola is currently exploring using [LinkML](https://linkml.io) or [yml2vocab](https://w3c.github.io/yml2vocab/) with [RDFS](../models/example.org/class.md) and (TBD) [Proto 3](https://protobuf.dev/programming-guides/proto3/) as its
Schema languages. It is conceptually open to supporting other kinds of schemas in the future; perhaps e.g.
[JSON Schema](https://github.com/enola-dev/enola/issues/313), or [CBOR's RFC #8610 CDDL](https://datatracker.ietf.org/doc/html/rfc8610) or [IPFS IPLD Schemas](https://ipld.io/docs/schemas/) (see #[777](https://github.com/enola-dev/enola/issues/777)) or [Cap’n Proto](https://capnproto.org/language.html), or [TypeScript](https://www.typescriptlang.org/docs/handbook/2/objects.html) (à la [Typson](https://github.com/lbovet/typson)), or [XML Schema](https://en.wikipedia.org/wiki/XML_Schema_(W3C)) (XSD) or [RelaxNG](https://relaxng.org), or [YANG](https://en.wikipedia.org/wiki/YANG) or [FHIR](https://www.hl7.org/fhir/) or [Varlink](https://varlink.org/Interface-Definition) or [Web IDL](https://webidl.spec.whatwg.org) or
[ASN.1](https://en.m.wikipedia.org/wiki/ASN.1) or [GNU poke](https://www.gnu.org/software/poke/) or other [Interface Description Languages (IDL)](https://en.m.wikipedia.org/wiki/Interface_description_language),
maybe even some of [those listed here](https://github.com/json-ld/yaml-ld/issues/19).
