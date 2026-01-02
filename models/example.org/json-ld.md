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

# JSON-LD

Are you tired of using 🐢 Turtles to write models? You are more familiar with JSON and YAML!

[JSON-LD](https://json-ld.org/) is a W3C standard to map JSON (and therefore YAML) to the RDF linked data model.

## YAML

We can rewrite the [`greeting2.ttl`](greeting2.ttl) from the [Linked Data](linked.md) chapter as this [`greeting2.yaml`](greeting2.yaml):

```yaml
{% include "./greeting2.yaml" start="# limitations under the License.\n" %}
```

Of course, we lost what uniquely identified our things... but JSON-LD Contexts can re-provide that, using this [`greeting-context.jsonld`](greeting-context.jsonld): <!-- TODO Write greeting-context.jsonld as greeting-context.yamlld ... -->

```json
{% include "./greeting-context.jsonld" %}
```

Combining these, Enola can recreate the same data model:

```bash cd .././.././..
$ ./enola get --load="models/example.org/greeting2.yaml?context=models/example.org/greeting-context.jsonld" https://example.org/greeting2
...
```

Generating documentation will look the same as in the original [Linked Data](linked.md) chapter, using this:

```bash cd .././.././..
$ ./enola docgen --load="models/example.org/greeting2.yaml?context=models/example.org/greeting-context.jsonld" --output=/tmp/models/ --no-index
...
```

## More

[Tools > JSON-LD](../../use/json-ld/index.md) has more reference documentation about JSON-LD in Enola.
