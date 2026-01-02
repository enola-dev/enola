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

# Tika

Enola in addition to RDF 🐢 TTL etc. can also read many other common file formats
(see [`enola info`](../use/info/index.md)).
This is courtesy of [Apache Tika](https://tika.apache.org),
see its [Supported Document Formats](https://tika.apache.org/3.0.0-BETA2/formats.html).

All of these formats are supported e.g. for conversions with [Rosetta](../use/rosetta/index.md) but also `--load` for [Get](../use/get/index.md), [DocGen](../use/docgen/index.md) and [Server](../use/server/index.md), etc.

<!-- TODO Markdown?! With links, not just Metadata? -->

<!-- NB: The following commands are not run through ExecMD! Add to test-cli.bash... -->

## HTML

```bash
./enola -v rosetta --in test/test.html --out="fd:2?mediaType=text/turtle"
```

This works for remote HTTP as well, of course:

```bash
./enola rosetta --http-scheme --in https://docs.enola.dev --out="fd:2?mediaType=text/turtle"
```

## Image

```bash
./enola -v get --http-scheme \
  --load https://www.vorburger.ch/michael_vorburger_portrait_in_bay_of_roses_spain_with_bad_lighting.jpg \
  https://www.vorburger.ch/michael_vorburger_portrait_in_bay_of_roses_spain_with_bad_lighting.jpg
```

## EPUB

Any `*.epub` (`application/epub+zip`).

## Executable

```bash
./enola -v rosetta --in /usr/lib64/libsane.so.1 --out="fd:2?mediaType=text/turtle"
```
