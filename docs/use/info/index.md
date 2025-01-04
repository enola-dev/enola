<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024-2025 The Enola <https://enola.dev> Authors

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

# Info

<!-- TODO Link? [The MediaType ("MIME") Model Graph]() might also interest you in this context. -->

[See Tika](../../concepts/tika.md) for more background about some of the formats listed here.

## Extensions

```bash cd ../.././..
$ ./enola info extensions
...
```

## Media Types

```bash cd ../.././..
$ ./enola info mediatypes
...
```

## Detect

```bash cd ../.././..
$ ./enola info detect --http-scheme https://www.google.com
...
```

or:

```bash cd ../.././..
$ ./enola info detect --file-scheme picasso.thing.yaml
...
```

Note that this file does not exist; that's fine,
as the type of its content is determined by the extension (in this case).
In some cases it may even be part of the URL itself, like for [`data:` URLs](../fetch/index.md#data):

```bash cd ../.././..
$ ./enola info detect "data:application/json;charset=UTF-8,%7B%22key%22%3A+%22value%22%7D"
...
```

It's also possible to "override" the Media Type, like this:

```bash cd ../.././..
$ ./enola info detect "picasso.thing.yaml?mediaType=application/json"
...
```

BTW: [`fetch`](../fetch/index.md) is the command to "get the bytes at" an URL.

## Metadata

```bash cd ../.././..
$ ./enola info metadata --load=test/metadata-label-property.ttl https://example.org/test-metadata-label-property
...
```

[Metadata](../../concepts/metadata.md) explains what this is about.

## Digest

```bash cd ../.././..
$ ./enola info digest --help
...
```

So for example:

```bash cd ../.././..
$ ./enola info digest --http-scheme https://www.google.com
...
```

This _digest_ can be used e.g. in [`?integrity=...` of `fetch`](../fetch/index.md#integrity).

## Screencast

![Demo](script.svg)
