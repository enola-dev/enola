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

# Connectors

Every _Entity Kind_ has a list of
[`connectors`](.././../dev/proto/core#connector). The
[`get` CLI](../get/index.md) (and [Web Server UI](../server/index.md))
automatically invoke each of these connectors to "augment" an _Entity._ (They
are called in the declared order, and each "stage" can "add on" to the
previous.)

<!-- There are different types of such connectors, each explained in one of the following sections. -->

## URI Templates

Models can contain `{...}` in the `uri_template` of `link` and the `paths` of
`related`.

Those are [URI Template](https://en.wikipedia.org/wiki/URI_Template) (from
[RFC 6570](https://datatracker.ietf.org/doc/html/rfc6570)). The available
variables are:

- `path.xyz` where _xyz_ is one of the _paths_ of the _EntityKind_

The templates are evaluated last, and not set if another _Connector_ has already
set a value for the respect `link` or `related`.

The demo on the [Get](../get/index.md) CLI documentation illustrates this connector.

## File System Repository

This connector complements entities by merging them with data from a file.

This is useful e.g. for testing, but also in production for "fixed" entities.

For example, this `model-fs.yaml`:

```yaml
{% include "./model-fs.yaml" %}
```

with this `demo.book_kind/0-13-140731-7.yaml`:

```yaml
{% include "./demo.book_kind/0-13-140731-7.yaml" %}
```

will cause:

```bash cd .././.././..
$ ./enola get --model file:docs/use/connector/model-fs.yaml demo.book_kind/0-13-140731-7
...
```

The File System Repository Connector [does not yet support the `Any` fields in `data`](https://github.com/enola-dev/enola/issues/238).

## gRPC

This connector complements entities by invoking a [remote gRPC](https://grpc.io) microservice
which implements [the ConnectorService API](.././../dev/proto/core#connectorservice). You can
(and should) implement this yourself, but just for illustration, let's use
[a trivial demo one](https://github.com/enola-dev/enola/blob/main/connectors/demo/src/main/java/dev/enola/demo/DemoConnector.java).
For example, this `model-grpc.yaml`:

```yaml
{% include "./model-grpc.yaml" %}
```

we can see it in action by running [`demo-grpc.bash`](demo-grpc.bash):

```bash
$ # ./demo-grpc.bash
...
```

In addition to returning entities themselves, gRPC Connectors also provide the Protocol Buffer
schemas (as `FileDescriptorProto`) for the `Any` fields in `data` of the _EntityKind_ they handle.

## Error

This connector always triggers an error, and is only intended for testing; e.g.
this `model-error.yaml`:

```yaml
{% include "./model-error.yaml" %}
```

will cause:

```bash $? cd .././.././..
$ ./enola get --model file:docs/use/connector/model-error.yaml demo.book_kind/0-13-140731-7
...
```

## Java

This connector complements entities by invoking a Java class "in-process".
To illustrate, we could e.g. directly use the
[internal implementation of the `error` connector](https://github.com/search?q=repo%3Aenola-dev%2Fenola+ErrorTestAspect.java&type=code)
like this:

```yaml
{% include "./model-java.yaml" %}
```

which will cause:

```bash $? cd .././.././..
$ ./enola get --model file:docs/use/connector/model-java.yaml demo.book_kind/0-13-140731-7
...
```

Using this connector with your own code requires extending Enola by linking it
as a Java library.

<!-- Please contact us for consulting services if you are interesting in pursuing this. -->

The recommended approach for integration is to write remote connectors instead.

## CLI

**TBD** [We may add](https://github.com/enola-dev/enola/issues/167) a generic
CLI Connector in the future, which could `exec`-ute a local command to complete
an _Entity._ (It would be customizable with templates for command arguments, and
the choice of JSON/YAML/Text- or Binary Proto on STDIN, and expected output
format of the completed Entity on STDOUT.)

## Timestamp

The `ts` of an _Entity_ is automatically set to the current time by a built-in
connector (which does not need to be declared) - unless another _Connector_ has
already set it, i.e. from data read from some "back-end". (E.g. the _File System
Repository_ does this based on the entity file's _last modified_ attribute.)
