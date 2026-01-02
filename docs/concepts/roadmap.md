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

# Roadmap 🛣️

1. Foundation
    1. Gen. Java Thing APIs
    1. [Things in YAML](https://github.com/enola-dev/enola/pull/607)
    1. Simpler Model Writing; convert existing models to new format
    1. Clean Up Tech Debt, incl. removing ancient original cruft

1. Models
    1. Bazel
    1. [Kubernetes](https://github.com/enola-dev/enola/issues/580)
    1. [Markdown](https://github.com/enola-dev/enola/issues/503)
    1. [Linux](https://github.com/enola-dev/enola/issues/738)
    1. [GitHub](https://github.com/enola-dev/enola/pull/477)
    1. [Java](https://github.com/enola-dev/enola/issues/727)
    1. [😺](https://github.com/enola-dev/enola/issues/611)

1. DocGen
    1. Ontologies split
    1. Class Properties Tables
    1. Class Diagrams! 📊 📈 🗠

1. Connectors
    1. `lsmem --json | ./enola get --load - --context lsmem.jsonld enola:/`
    1. `lshw -json` ditto with a `lshw.jsonld`
    1. GitHub REST API
    1. [Exec](https://github.com/enola-dev/enola/issues/167)
    1. gRPC

1. [Stores & Query](other.md#persistence)
    1. `--store memory:` (default) - no queries
    1. `--store memory:rdf --query "SELECT * WHERE { ?s ?p ?o }"`
    1. `--load greeting1.ttl --store lmdb:greetings.db/`
    1. Graph Databases?

1. Inference

1. Web UI
    1. [HTMx](https://htmx.org)
    1. Query Tab
    1. Timeline View

1. [ML 🔮](singularity.md)
