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

# Info ℹ️

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

```bash $? cd ../.././..
$ ./enola info digest --help
...
```

So for example:

```bash cd ../.././..
$ ./enola info digest --http-scheme https://www.vorburger.ch/hello.md
...
```

Or alternatively:

```bash cd ../.././..
$ ./enola info digest --base=Base64Pad --type=sha2_256 --http-scheme https://www.vorburger.ch/hello.md
...
```

Or, just _"for fun",_ also:

```bash cd ../.././..
$ ./enola info digest --base=Base256Emoji --http-scheme https://www.vorburger.ch/hello.md
...
```

Any of these _digests_ can be used e.g. in [`?integrity=...` of `fetch`](../fetch/index.md#integrity).

## Change

```bash $? cd ../.././..
$ ./enola info change --help
...
```

Issue an (opaque) _"change token"_ for a URL, which for HTTP may use e.g. ETags:

```bash cd ../.././..
$ ./enola info change --http-scheme https://www.vorburger.ch/hello.md | tee /tmp/helloChangeToken.txt
...
```

and verify if the content at the URL has changed:

```bash cd ../.././..
$ ./enola info change --http-scheme https://www.vorburger.ch/hello.md $(cat /tmp/helloChangeToken.txt)
...
```

Or for files, for which this may be implemented with last modified and size metadata, and hashing content:

```bash cd ../.././..
$ echo "hello," >/tmp/hello.txt
...
```

and now as above:

```bash cd ../.././..
$ ./enola info change /tmp/hello.txt | tee /tmp/helloChangeToken.txt
...
```

we have not yet changed it:

```bash cd ../.././..
$ ./enola info change /tmp/hello.txt $(cat /tmp/helloChangeToken.txt)
...
```

but now let's change it:

```bash cd ../.././..
$ echo "world" >>/tmp/hello.txt
...
```

and check again:

```bash cd ../.././..
$ ./enola info change /tmp/hello.txt $(cat /tmp/helloChangeToken.txt)
...
```

## Screencast

![Demo](script.svg)
