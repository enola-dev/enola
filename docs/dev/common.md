<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025-2026 The Enola <https://enola.dev> Authors

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

# Enola.dev Common Java Framework

Enola's code base includes _"common"_ technical framework components which are not specific to Enola:

1. Agent Development Kit (ADK) Java utilities
1. [Secret](https://docs.enola.dev/dev/javadoc/dev/enola/common/secret/package-summary.html): A credentials managers, with e.g. [`age`](https://age-encryption.org) and [`pass`](https://www.passwordstore.org) etc. integration
1. [Context](https://docs.enola.dev/dev/javadoc/dev/enola/common/context/package-summary.html): Implicit context passing, via Thread Local and `ScopedValue` (JEP 446 - TBD)
1. [Functional](https://docs.enola.dev/dev/javadoc/dev/enola/common/function/package-summary.html), notably [`MoreStreams`](https://docs.enola.dev/dev/javadoc/dev/enola/common/function/MoreStreams.html) with the [`Sneaker`](https://docs.enola.dev/dev/javadoc/dev/enola/common/function/Sneaker.html)
1. I/O: [Resource](https://docs.enola.dev/dev/javadoc/dev/enola/common/io/resource/package-summary.html), with [URL utils](https://docs.enola.dev/dev/javadoc/dev/enola/common/io/iri/package-summary.html), the [Media Types](https://docs.enola.dev/dev/javadoc/dev/enola/common/io/mediatype/package-summary.html) and [hashing](https://docs.enola.dev/dev/javadoc/dev/enola/common/io/hashbrown/package-summary.html)
1. [Convert](https://docs.enola.dev/dev/javadoc/dev/enola/common/convert/package-summary.html): Extensible Java object conversion framework

These are already available via [our Maven Repo](maven.md), as seen e.g. in the [JBang](jbang.md) usage examples.

[See the JavaDoc](javadoc/index.html).
