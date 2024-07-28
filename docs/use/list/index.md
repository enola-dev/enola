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

# List Entities

**NOTE:** *The `list` command will eventually be entirely replaced by [`get enola:/`](../get/index.md#list)*

`enola list` will retrieve a list of *entities* from [connectors](../connector/index.md).

<!--

    THIS DOESN'T ACTUALLY WORK - BECAUSE model.yaml HAS NO CONNECTORS WHICH LIST ANYTHING!

## List Books

```bash cd ../.././..
$ ./enola list --model docs/use/library/model.yaml demo.book
...
```
-->

## List Kinds

Because Entity Kinds are Entities themselves, you can also list them like this, with details:

```bash cd ../.././..
$ ./enola list --model docs/use/library/model.yaml --format=yaml enola.entity_kind
...
```

## List Built-In Kinds

Note how the section above showed some additional entity kinds, in addition to those from the example.
It's possible to list only those built-in entity kinds, using an "empty" model URI, like this:

```bash cd ../.././..
$ ./enola list --model "empty:?mediaType=application/json" enola.entity_kind
...
```

To get a list of only the names of Entity Kinds, just use e.g. [`yq`](https://github.com/mikefarah/yq).

The `enola.schema` kind listed above is explained in [the Schema section](schema.md).

## Screencast (Asciinema)

![Demo](script.svg)
