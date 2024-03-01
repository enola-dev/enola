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

# Hello

Please first [install Enola](../../../use/), if you haven't already.

Then let's do a [`hello, world`](https://en.wikipedia.org/wiki/%22Hello,_World!%22_program)!

## Type

Greetings are a kind of _Type._ Let's define one, in [`hello.type.yaml`](hello.type.yaml):

<!-- TODO Include the YAML, without the LICENSE header -->

```yaml
...
```

Now let's use this:

```bash
./enola --model hello.type.yaml get hello/world
world
```

<!-- TODO Make ExecMD a testing tool, and FAIL if output is not as inlined & excepted -->

## Schema

Our first `enola.dev/demo/hello` _"type"_ was just a `string`. That was a great start - but now let's define our first _data structure._ We'll use [Protocol Buffers](https://protobuf.dev) (for now, [later others](../../../concepts/core.md#schemas)) to define a _"schema",_ in [`greeting.proto`](greeting.proto):

<!-- TODO Include the Proto, without the LICENSE header -->

```proto
...
```

and use this by changing that `schema: string:` from above to `proto: dev.enola.demo.hello.Greeting` as in [`greeting.type.yaml`](greeting.type.yaml), and then:

```bash
./enola --model greeting.type.yaml get hello/world
message: world
```

The output is no longer just text, but now structured information - the `message` is `world`. This is YAML format. Enola also supports [other _formats_](../../../concepts/core.md#formats), e.g. as JSON:

```bash
./enola --model greeting.type.yaml get --format=json hello/world
{ message: world }
```

## Template

TODO Initially not for link but just set `message` to "hello, {{{ greeting }}}" prefix (change `uri: hello/{greeting}`)

## Link

TODO [`linked.proto`](linked.proto), with an `enola:` URI template, and here introduce the Web UI!!!

https://docs.enola.dev/use/connector/#uri-templates

## Connector

https://docs.enola.dev/use/connector/#file-system-repository

## Reflection

```bash
./enola --model greeting.type.yaml get enola/type/greeting
name: enola.dev/demo/hello
uri: hello/{message}
schema:
  proto: dev.enola.demo.hello.Greeting
```

Open in the Web interface, and note link to `Greeting`.
