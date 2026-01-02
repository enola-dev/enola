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

# Rosetta 🌐

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
* [Many other formats](#tika)!
* [XML](#xml)

Specifying the `--schema` flag is optional for YAML <=> JSON conversion, but required for TextProto.

Rosetta transforms a (single) input resource into one output resource of another format.
Alternatively,
[generate](../gen/index.md) can load (possibly several) resources
(or "logical IRIs") which contain _Things_ and transform them into (some of) these formats.

## Graph Diagrams

Enola can generate [Graph Diagrams like this](../../models/example.org/graph.md), through [DocGen](../docgen/index.md) (see
[Tutorial](../../models/example.org/graph.md)), or Rosetta.

### Graphviz

Similarly to e.g. [rdflib](https://rdflib.readthedocs.io/)'s `rdf2dot` (and `rdfs2dot`):

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out "docs/BUILT/picasso.gv?full=true" && dot -Tsvg -O docs/BUILT/picasso.gv
...
```

produces [`picasso.gv`](../../BUILT/picasso.gv) and [`picasso.gv.svg`](../../BUILT/picasso.gv.svg):

![Graph of Painters](../../BUILT/picasso.gv.svg)

The `full` URL query string can be used to control appearance, and `--no-file-loader` suppresses the file; see:

```bash cd ../.././..
$ ./enola rosetta --no-file-loader --in test/picasso.ttl --out "docs/BUILT/picasso-small.gv?full=false" && dot -Tsvg -O docs/BUILT/picasso-small.gv
...
```

![Smaller Graph of Painters](../../BUILT/picasso-small.gv.svg)

<!--
### Graph Commons

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out /tmp/picasso.graphcommons.json
...
```

produces a JSON which can be imported into [GraphCommons.com](https://graphcommons.com/).
-->
### GEXF

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out docs/BUILT/picasso.gexf
...
```

produces [`picasso.gexf`](../../BUILT/picasso.gexf) in [GEXF format](https://gexf.net) (see also [GEXF examples](https://github.com/graphology/graphology/tree/master/src/gexf/test/resources)), which can be opened e.g. in [Gephi Lite](https://gephi.org/gephi-lite/) or [Retina](https://ouestware.gitlab.io/retina/).

## Tika

[See here](../../concepts/tika.md)!

## YAML to JSON

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.yaml --out /tmp/picasso.json
...
```

## Turtle 🐢 to JSON-LD 🔗

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out /tmp/picasso.jsonld
...
```

## JSON & YAML to Turtle 🐢 via JSON-LD

[See JSON-LD](../json-ld/index.md).

## XML

```bash cd ../.././..
$ ./enola rosetta --in test/greeting1-nested.xml --out="fd:1?mediaType=text/turtle"
...
```

## Turtle 🐢 to Things ⛓️

```bash cd ../.././..
$ ./enola rosetta --in test/picasso.ttl --out /tmp/picasso.thing.yaml
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
$ ./enola rosetta --in 'docs/use/rosetta/hello.txt?charset=UTF-8' --out '/tmp/hello-windows.txt?charset=ISO-8859-1'
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
