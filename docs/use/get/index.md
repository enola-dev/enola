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

# Get Entity

`enola get` will retrieve an _entity_ from its [connectors](../connector/index.md).

## Screencast (Asciinema)

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
$ ./enola get --load models/enola.dev/enola.ttl https://enola.dev/emoji | head --lines=3
...
```

## Get Book

Get a `book` - note how the _related_ `kind` and `library` ID are set, based on the template
[from the model](../library/index.md):

```bash cd ../.././..
$ ./enola get --model docs/use/library/model.yaml demo.book/ABC/0-13-140731-7/1
...
```

Get a `book_kind` - note how the `google` _link_ was set, based on the template
[from the model](../library/index.md):

```bash cd ../.././..
$ ./enola get --model docs/use/library/model.yaml demo.book_kind/0-13-140731-7
...
```

If the entity contains `data` fields, the `Any` protos are displayed correctly "unwrapped".
This can be seen in [the gRPC Demo Connector](../connector/index.md#grpc).
