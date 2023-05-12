<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

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

Rosetta, inspired by
[the Rosetta Stone](https://en.wikipedia.org/wiki/Rosetta_Stone), transforms
between [`YAML`](https://yaml.org) ⇔ [`JSON`](https://www.json.org) ⇔
[`TextProto`](https://protobuf.dev/reference/protobuf/textformat-spec/) formats:

```bash cd .././.././..
$ ./enola rosetta --in=file:docs/use/library/model.yaml --out=file:docs/use/library/model.json --schema=EntityKinds
...
```

The `model.json` file now contains:

```json
{% include "../library/model.json" %}
```

Similarly with:

```bash cd .././.././..
$ ./enola rosetta --in=file:docs/use/library/model.yaml --out=file:docs/use/library/model.textproto --schema=EntityKinds
...
```

The `model.textproto` file now contains:

```yaml
{% include "../library/model.textproto" %}
```
