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

# Get Thing ⬇️

## Screencast

![Demo](script.svg)

## List

Get the list of all available Thing (Template) IRIs, using the special `enola:/` IRI:

```bash cd ../.././..
$ ./enola get --load "models/**.ttl" enola:/
...
```

## Get 🐢

Get something from a loaded 🐢 Turtle resource, e.g. from the [enola.dev/enola.ttl](../../models/enola.dev/enola.ttl) model:

```bash cd ../.././..
$ ./enola get --load models/enola.dev/enola.ttl https://enola.dev/emoji
...
```

Note that `get` [supports various formats](../help/index.md#get).

PS: The [`fetch`](../fetch/index.md) command does something related.
