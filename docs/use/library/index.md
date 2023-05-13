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

# Library Model

Enola's _Entities_ are modeled with _Entity Kinds,_ as described [in the architecture](../../concepts/core-arch.md).

Different formats are supported, and can be converted using [Rosetta](../rosetta/index.md).

## `model.yaml`

```yaml
{% include "model.yaml" %}
```

## List Kinds

You can query Enola for a list of known Entity Kind names:

```bash cd .././.././..
$ ./enola list-kinds --model file:docs/use/library/model.yaml
...
```

Because Entity Kinds are Entites themselves, you can also list them like this, with details:

```bash cd .././.././..
$ ./enola list --model file:docs/use/library/model.yaml --format=yaml enola.entity_kind
...
```

## Screencast (Asciinema)

![Demo](script.svg)
