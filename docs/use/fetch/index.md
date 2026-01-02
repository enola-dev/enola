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

# Fetch ⤵️

`fetch` fetches a _Resource_ from an
_URL_ and outputs its content. You can therefore use this similarly to [curl](https://curl.se/) or [httpie](https://httpie.io/cli) or [wget](https://en.wikipedia.org/wiki/Wget). (If you want to see the
_Media Type,_ use [`info detect`](../info/index.md#detect).)

This is different from [`get`](../get/index.md), which shows _Thing/s_ given an _IRI.
(However the `--load` option of `get` internally does a `fetch`, and supports the same schemes.)

Enola supports the [URI schemes](https://en.wikipedia.org/wiki/List_of_URI_schemes) which are documented below.
These are supported everywhere; including in `fetch`, `--load`, and elsewhere.

## Schemes

### Files

We can do `cat`-like equivalent of local files using [the
`file:` scheme](https://en.wikipedia.org/wiki/File_URI_scheme):

```bash cd ../.././..
$ echo "hello, world" >/tmp/hi.txt && ./enola fetch file:///tmp/hi.txt
...
```

We can omit the `file:` scheme and use absolute or relative paths,
because (in the CLI) the current working directory is implicitly [the _base URI_
used to resolve URI references](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#URI_references):

```bash cd ../.././..
$ ./enola fetch /tmp/hi.txt
...
```

When running a remotely accessible [server](../server/index.md), you'll most probably want to disable the
`file:` scheme,
to block access to local files for security:

```bash $? cd ../.././..
$ ./enola fetch --no-file-scheme /tmp/hi.txt
...
```

### HTTP

This will fetch <https://www.vorburger.ch/hello.md>: _(Note how for security reasons we have to explicitly permit it.)_

```bash cd ../.././..
$ ./enola fetch --http-scheme https://www.vorburger.ch/hello.md
...
```

Enola locally caches HTTP responses on the filesystem.

### IPFS

Enola, like e.g. [`curl`](https://curl.se), has a _"native protocol handler"_
to [support `ipfs:` URLs for decentralized content from IPFS](https://ipfs.tech/):

```bash $% cd ../.././..
$ ./enola fetch --ipfs-gateway=https://dweb.link/ipfs/ ipfs://QmXV7pL1CB7A8Tzk7jP2XE9kRyk8HZd145KDptdxzmNLfu
...
```

The `--ipfs-gateway` is the URL of an [IPFS HTTP Gateway](https://docs.ipfs.tech/reference/http/gateway/).
Instead of the shown `dweb.link`,
we do (highly) recommend that you [locally install & run](https://docs.ipfs.tech/install/) an _IPFS Node,_
such as [IPFS Desktop](https://docs.ipfs.tech/install/ipfs-desktop/),
or [Kubo](https://docs.ipfs.tech/install/command-line/),
and then use `--ipfs-gateway=http://localhost:8080/ipfs/` instead.

For initial testing you can specify one of the [public IPFS Gateways](https://ipfs.github.io/public-gateway-checker/)
(or "rent" one from a provider such as [Pinata](https://pinata.cloud/dedicated-ipfs-gateways) or [Infura](https://www.infura.io/product/ipfs) or [Cloudflare](https://www.cloudflare.com/application-services/products/web3/)).
However, we do **NOT** recommend using these for anything "real",
because of the following disadvantages:

* Local Nodes make great local caches to improve Enola's performance
* Public gateways are likely to rate limit your HTTP requests and return 403 errors
* Enola does not verify the CID hash over the fetched content, so you are implicitly trusting the Gateway (and assume no [MITM](https://en.wikipedia.org/wiki/Man-in-the-middle_attack))
* Running an IPFS Node, especially a permanently on and publicly IP reachable one, furthers the global IPFS network

<!-- PS: IPFS URLs typically don't work directly in your web browser - unless you install the [IPFS Companion Browser Extension](https://docs.ipfs.tech/install/ipfs-companion/).

PPS: Note that the `//` separator after `ipfs:` is mandatory;
we (intentionally) do not support `ipfs:Qm..` (or `ipfs:/Qm..`), nor e.g. `dweb://ipfs/Qm..`.
For background about why this is so,
see https://github.com/ipfs/in-web-browsers/blob/68483d0bbe014d0626ca28b6e0d224341c1e8b8f/ADDRESSING.md and the conclusion of https://github.com/ipfs/kubo/issues/1678#issuecomment-492977659. -->

### Classpath

```bash cd ../.././..
$ ./enola fetch classpath:/VERSION
...
```

### Data

Enola [supports (RFC 2397) `data:` URLs](https://en.m.wikipedia.org/wiki/Data_URI_scheme):

```bash cd ../.././..
$ ./enola fetch "data:application/json;charset=UTF-8,%7B%22key%22%3A+%22value%22%7D"
...
```

### Multibase

Enola [supports](https://github.com/multiformats/multibase/issues/134)
`multibase:` URLs, which are from [Multiformats](https://multiformats.io/):

```bash cd ../.././..
$ ./enola fetch multibase:maGVsbG8sIHdvcmxk
...
```

Nota bene: This (fetchable) `multibase:` scheme is (intentionally) different from the
`mb:` used for (random) Thing IRIs.

### File Descriptor

`fd:` is a (non-standard) URL scheme in Enola for reading from or writing to [file descriptors](https://en.wikipedia.org/wiki/File_descriptor), for:

* `fd:0` [STDIN](https://en.wikipedia.org/wiki/Stdin)
* `fd:1` [STDOUT](https://en.wikipedia.org/wiki/Stdout)
* `fd:2` [STDERR](https://en.wikipedia.org/wiki/Stderr)

The _Media Type_ of this special resource will be `application/octet-stream` (**not** `application/binary`),
unless there is [a `?mediaType=` parameter](#media-type).

The _Charset_ will be the default of the JVM,
unless there is (checked first) [a `?charset=` parameter](#charset) (e.g. `fd:0?charset=UTF-16BE`),
or the `?mediaType=` parameter includes a charset (e.g. `fd:1?mediaType=application/yaml;charset=utf-16be`).

<!-- If updating ^^^ then also update JavaDoc of dev.enola.common.io.resource.FileDescriptorResource -->

<!-- TODO Support '-' as special URI shortcut for fd:0 STDIN? -->

### Empty

`empty:` is another (non-standard) URL scheme in Enola for "no content" (as an alternative to `data:,`):

```bash cd ../.././..
$ ./enola fetch empty:/
...
```

### Teapot 🫖

We are proud to handle [RFC 2324](https://www.rfc-editor.org/rfc/rfc2324.html)-inspired
🫖 `coffee:` etc. URLs, in support of [save418.com](https://save418.com/):

```bash cd ../.././..
$ ./enola fetch "kafo://demo.enola.dev/pot-7?#syrup-type=Vanilla"
...
```

All international (§3.) coffee URI schemes are fully supported; and e.g.
`kafo://demo.enola.dev/pot-7?#syrup-type=Vanilla` is valid for sameideanoj. Please note
the following (major and breaking, sorry) backwards incompatibility: Due to what is assumed to
be a typo in the original RFC, for _Catalan, French and Galician_ the correctly accented URI
scheme `café` ("caf%C3%A9") instead of `cafè` ("caf%C3%E8") is used. However, we
do also support `cafè` as the _Italian_ URI scheme; this should help to avoid major
systems interoperability disasters. (It may still cause minor language issues; where full
interop with the original RFC spec is required, we recommend using a Locale override URL
parameter; e.g. `cafè://demo.enola.dev/pot-7?hl=fr`.)

### Exec

TODO We plan to support an `exec:` scheme, whose content will be the resulting of running the given command,
similar to e.g. [🐪 Camel's](https://camel.apache.org/components/4.8.x/exec-component.html) or (vaguely) Web Browsers'
`javascript:`.

<!-- TODO ### Git `git:` ? -->

## Parameters

Enola considers certain generic query parameters in URLs it fetches.

These work with most but not all schemes (e.g. `data:` does not permit query parameters).

### Media Type

Adding e.g. `?mediaType=application/json` [overrides the Media Type](../info/index.md#detect)
which e.g. a server provided in e.g. a HTTP header,
or that was determined from a file extension.

### Charset

Adding e.g. `?charset=iso-8859-1` overrides (and takes precedence over)
the Charset from the Media Type (if any) or any HTTP header like mechanisms.

<!-- ### Locale Adding e.g. `?hl=gsw-CH` overrides the _Locale_ used for textual responses to [Swiss German](https://en.wikipedia.org/wiki/Swiss_German). -->

### Integrity

Adding
`?integrity=...` verifies resources via a [cryptographic digest ("hash")](https://docs.ipfs.tech/concepts/hashing/)
using a [Multiformats's Multibase encoded Multihash](https://www.multiformats.io).
(This is similar e.g. to [HTML's Subresource Integrity (SRI)](https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity).)
It works for all schemes:

```bash cd ../.././..
$ ./enola fetch "/tmp/hi.txt?integrity=z8VxiEEn4n7uuGrVQjeoH2KYypytUHttCubqN7rr65xSH3wjLDjHciXuTyTHkoRuJT1Njghj68RQdynADQt9vzLgyEs"
...
```

or:

```bash cd ../.././..
$ ./enola fetch --http-scheme "https://www.vorburger.ch/hello.md?integrity=z8VttgvnrXN5ZzqAh8BLwyup7htUmSM9gbKR445teEECTwMRDQTireiWgWauLiZ4Xr5esrqbVFNbAuAM2XyZ4CTxU7N"
...
```

In order to find the expected Multibase encoded Multihash,
it's simplest to once use a wrong one, and then replace it with the correct one which is shown by the error message:

```bash $? cd ../.././..
$ ./enola fetch --http-scheme "https://www.vorburger.ch/hello.md?integrity=z8VsnXyGnRwJpnrQXB8KcLstvgFYGZ2f5BCm3DVndcNZ8NswtkCqsut69e7yd1FKNtettjgy669GNVt8VSTGxkAiJaB"
...
```

[`enola info digest`](../info/index.md#digest) is an alternative for obtaining the `?integrity=...` value.

Note that while [Multihash](https://www.multiformats.io/multihash/) defines codes for [various hash functions](https://github.com/multiformats/multicodec/blob/master/table.csv),
Enola (currently) [intentionally](https://github.com/google/guava/issues/5990#issuecomment-2571350434) only actually supports
`sha2-256` & `sha2-512`.

[URL Integrity Spec](../../specs/url-integrity/index.md) describes this further.

<!-- TODO ?cache from OptionalCachingResourceProvider (current un-used) -->

<!--
## Screencast

![Demo](script.svg)
-->
