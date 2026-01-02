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

# Graphs

Enola can [visualize graphs](../../concepts/graph.md) of Things in _"network"_ diagrams with [Rosetta](../../use/rosetta/index.md#graphviz) and [DocGen](../../use/docgen/index.md).

## Graphviz

```bash cd ../../..
$ ./enola rosetta --in docs/models/example.org/greeting3.ttl --out=docs/BUILT/greetings3.gv && dot -Tsvg -O docs/BUILT/greetings3.gv
...
```

produces this:

![Tutorial Graph](../../BUILT/greetings3.gv.svg)

## Styles

The <https://enola.dev/color> and <https://enola.dev/text-color> predicates can be used to style graphs.
