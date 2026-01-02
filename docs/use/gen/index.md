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

# Generate 🏗️

Generates [various outputs](../help/index.md#gen) from (loaded) _Things._

Many of these formats "mirror" the [respective support in Rosetta](../rosetta/index.md).
The difference is that Rosetta transforms one resource into another one,
whereas this generates (possibly several) output/s from possibly several input/s.

## Sigma (TODO)

We have plans to support https://www.sigmajs.org.

## Graphviz

Based on [Rosetta's Graphviz support](../rosetta/index.md#graphviz):

```bash cd ../.././..
$ ./enola gen graphviz --no-file-loader --load=enola:TikaMediaTypes --output /tmp/
...
```

Produces a (rather huge...) `graphviz.gv` which can then be rendered to an (ugly!) SVG e.g. using:

    dot -Ksfdp -Tsvg -O /tmp/graphviz.gv

## GEXF

See [Rosetta's GEXF support](../rosetta/index.md#gexf).

<!--
## Graph Commons (TODO)

See [Rosetta's Graph Commons support](../rosetta/index.md#graph-commons).

    ./enola rosetta --in enola:TikaMediaTypes --out /tmp/TikaMediaTypes.graphcommons.json

-->

## Markdown (TODO)

[The (old) `docgen`](../docgen/index.md) will later be integrated into a (new) generic `gen markdown`.
