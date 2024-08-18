<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2024 The Enola <https://enola.dev> Authors

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

# Rosetta

<!-- Show (include) each example's input/output... maybe split onto separate pages? -->

Rosetta, inspired by
[the Rosetta Stone](https://en.wikipedia.org/wiki/Rosetta_Stone), transforms
e.g. between:

* Text Encodings (e.g. [UTF-8](https://en.wikipedia.org/wiki/UTF-8) ⇔ [ISO 8859-1](https://en.wikipedia.org/wiki/ISO/IEC_8859-1) etc.)
* RDF Turtle ⇔ JSON-LD ⇔ YAML-LD ⇔ Enola Things
* YAML & JSON ⇔ RDF Turtle [with JSON-LD Contexts](../json-ld/index.md)
* [`YAML`](https://yaml.org) ⇔ [`JSON`](https://www.json.org) ⇔
  [`TextProto`](https://protobuf.dev/reference/protobuf/textformat-spec/) ⇔
  _[Binary Protocol Buffer "Wire"](https://protobuf.dev/programming-guides/encoding/)_ formats
* Graph Diagrams from RDF et al.

Specifying the `--schema` flag is optional for YAML <=> JSON conversion, but required for TextProto.

## Graph Diagrams

Enola can generate [Graph Diagrams like this](../../models/example.org/graph.md), through [DocGen](../docgen/index.md) (see
[Tutorial](../../models/example.org/graph.md)), or Rosetta.

### Graphviz

Similarly to e.g. [rdflib](https://rdflib.readthedocs.io/)'s `rdf2dot` (and `rdfs2dot`):

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out docs/BUILT/picasso.gv && dot -Tsvg -O docs/BUILT/picasso.gv
...
```

produces [`picasso.gv`](../../BUILT/picasso.gv) and [`picasso.gv.svg`](../../BUILT/picasso.gv.svg):

![Graph of Painters](../../BUILT/picasso.gv.svg)

### GEXF

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out docs/BUILT/picasso.gexf
...
```

produces [`picasso.gexf`](../../BUILT/picasso.gexf) in [GEXF format](https://gexf.net), which can be opened e.g. in [Gephi Lite](https://gephi.org/gephi-lite/).

## YAML to JSON

```bash cd ../.././..
$ ./enola rosetta --in=docs/use/library/model.yaml --out=docs/use/library/model.json --schema=EntityKinds
...
```

The `model.json` file now contains:

<!-- @formatter:off -->
```json
{% include "../library/model.json" %}
```
<!-- @formatter:on -->

## YAML to TextProto

```bash cd ../.././..
$ ./enola rosetta --in=docs/use/library/model.yaml --out=docs/use/library/model.textproto --schema=EntityKinds
...
```

The `model.textproto` file now contains:

<!-- @formatter:off -->
```yaml
{% include "../library/model.textproto" %}
```
<!-- @formatter:on -->

## YAML to Binary Protocol Buffer

```bash cd ../.././..
$ ./enola rosetta --in=docs/use/library/model.yaml --out=docs/use/library/model.binpb --schema=EntityKinds
...
```

The `model.binpb` now contains
_[binary protocol buffer wire format](https://protobuf.dev/programming-guides/encoding/)._

## Turtle 🐢 to JSON-LD 🔗

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out picasso.jsonld
...
```

## JSON & YAML to Turtle 🐢 via JSON-LD

[See JSON-LD](../json-ld/index.md).

## Turtle 🐢 to Things ⛓️

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out picasso.thing.yaml
...
```

## [UTF-8](https://en.wikipedia.org/wiki/UTF-8) to [ISO 8859-1](https://en.wikipedia.org/wiki/ISO/IEC_8859-1)

```bash cd ../.././..
$ file docs/use/rosetta/hello.txt
...
```

```bash cd ../.././..
$ cat docs/use/rosetta/hello.txt
...
```

```bash cd ../.././..
$ ./enola rosetta --in='docs/use/rosetta/hello.txt?charset=UTF-8' --out='/tmp/hello-windows.txt?charset=ISO-8859-1'
...
```

```bash cd ../.././..
$ file /tmp/hello-windows.txt
...
```

```bash cd ../.././..
$ cat /tmp/hello-windows.txt
...
```
