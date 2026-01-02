<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024-2026 The Enola <https://enola.dev> Authors

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

# Namespaces

Enola supports _Namespaces_ with _prefixes_ for shorter and thus more easily human readable [CURIEs](https://en.wikipedia.org/wiki/CURIE).

Several of the Enola's [formats](core.md#formats), notably both [🐢 Turtle](turtle.md) and [XML](xml.md), can read and write with namespace support.

Formats with namespace support include a custom syntax to declare namespaces (`@prefix` and `xmlns:`), which is used when reading them. This is purely an I/O convenience; in the internal data store those prefixes are not used.

When showing things on the UI, or when writing out things in formats with namespace support, Enola uses its _active_ prefix map. The supported namespaces are currently [hard-coded](https://github.com/enola-dev/enola/blob/main/java/dev/enola/common/io/iri/namespace/NamespaceRepositoryEnolaDefaults.java).

The plan is to make the active prefix to namespace URI mappings fully configurable in the future, likely using [`https://enola.dev/namespaces`](https://docs.enola.dev/models/enola.dev/namespaces/).

<!-- Link to models directory of all vocabulary & ontologies. -->

## Design

Enola recommends using _[slash namespaces](https://www.w3.org/2001/sw/BestPractices/VM/http-examples/2006-01-18/#slash)_ instead of _[hash namespaces](https://www.w3.org/2001/sw/BestPractices/VM/http-examples/2006-01-18/#hash)_ for new vocabularies.

In our experience, this is less confusing in some situations (e.g. [TTL BASE](turtle.md#base)).
