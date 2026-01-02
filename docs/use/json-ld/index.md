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

# JSON-LD ➰

Enola supports [JSON-LD](https://en.wikipedia.org/wiki/JSON-LD).

## Direct Load

Enola can directly `--load` JSON & YAML, given a JSON-LD Context; [see Tutorial](../../models/example.org/json-ld.md).

## Conversions

[Enola Rosetta](../rosetta/index.md) can convert model resources among different formats using JSON-LD; e.g. from `picasso.yaml`:

```yaml
{% include "../../../test/picasso.yaml" start="# limitations under the License.\n" %}
```

or from `picasso.json`:

```json
{% include "../../../test/picasso.json" %}
```

with a `picasso-context.jsonld`:

```json
{% include "../../../test/picasso-context.jsonld" %}
```

### YAML to RDF Turtle 🐢

```bash cd ../.././..
$ ./enola rosetta --in="test/picasso.yaml?context=test/picasso-context.jsonld" --out="fd:1?mediaType=text/turtle"
...
```

### JSON to RDF Turtle 🐢

```bash cd ../.././..
$ ./enola rosetta --in="test/picasso.json?context=test/picasso-context.jsonld" --out="fd:1?mediaType=text/turtle"
...
```

### JSON to JSON-LD

```bash cd ../.././..
$ ./enola rosetta --in="test/picasso.json?context=test/picasso-context.jsonld" --out="fd:1?mediaType=application/ld+json" | head -7
...
```

## Tips

### String to Link

Use something like this to map a string, e.g. a machine hostname in a JSON, to a link in RDF:

```json
{
    "@context": {
        "@version": 1.1,
        ...
        "machine": {
            "@id": "http://example.org/host",
            "@type": "@id",
            "@context": {
                "@base": "http://example.org/host/"

```

### Override Nested

In order to _"override"_ the mapping for a _"nested"_ JSON property, JSON-LD lets us define _embedded
sub-contexts,_ for example like this, if some _"contained"_ `id` is not really an `@id`:

```json
{
    "@context": {
        "@version": 1.1,
        ...
        "id": "@id",
        "something": {
            "@context": {
                "id": "id"
```

You could also consider using `@propagate false` in the context.
