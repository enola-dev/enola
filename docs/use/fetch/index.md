<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024 The Enola <https://enola.dev> Authors

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

# Fetch

`fetch` fetches a _Resource_ from an _URL_ and outputs its content. You can therefore use this similarly to [curl](https://curl.se/) or [httpie](https://httpie.io/cli) or [wget](https://en.wikipedia.org/wiki/Wget). (If you want to see the _Media Type,_ use [`info detect`](../info/index.md#detect).)

This is different from [`get`](../get/index.md), which shows _Things_ given an _IRI.
(However the `--load` option of `get` internally does a `fetch`, and supports the same schemes.)

Enola supports the [URI schemes](https://en.wikipedia.org/wiki/List_of_URI_schemes) which are documented below.
These are supported everywhere; including in `fetch`, `--load`, and elsewhere.

## HTTP

This will fetch <https://www.vorburger.ch/hello.md>: _(Note how for security reasons we have to explicitly permit it.)_

```bash cd ../.././..
$ ./enola fetch --http-scheme https://www.vorburger.ch/hello.md
...
```

Enola locally caches HTTP responses on the filesystem.

## Files

We can do `cat`-like equivalent of local files using [the `file:` scheme](https://en.wikipedia.org/wiki/File_URI_scheme):

```bash cd ../.././..
$ echo "hello" >/tmp/hi.txt && ./enola fetch file:///tmp/hi.txt
...
```

We can omit the `file:` scheme and use absolute or relative paths,
because (in the CLI) the current working directory is implicitly [the _base URI_
used to resolve URI references](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#URI_references):

```bash cd ../.././..
$ ./enola fetch /tmp/hi.txt
...
```

## Classpath

```bash cd ../.././..
$ ./enola fetch classpath:/VERSION
...
```

## Data

Enola [supports (RFC 2397) `data:` URLs](https://en.m.wikipedia.org/wiki/Data_URI_scheme):

```bash cd ../.././..
$ ./enola fetch "data:application/json;charset=UTF-8,%7B%22key%22%3A+%22value%22%7D"
...
```

## Empty

`empty:` is a (non-standard) URL scheme in Enola for "no content" (as an alternative to `data:,`):

```bash cd ../.././..
$ ./enola fetch empty:/
...
```

<!--
## Screencast

![Demo](script.svg)
-->
