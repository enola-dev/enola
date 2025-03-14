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

# Identifiers

<!-- TODO build.bash: cp -r docs/ .be/docs/ && ./enola semmd --in=.be/docs/**.md --out=.be/docs/
     (BTW The semmd sub-command is just a shortcut for exactly the same as: ./enola rosetta --in=.be/docs/**.md?mediaType=text/x-semmantic-markdown --out=.be/docs/?mediaType=text/markdown?kind=CommonMark)

  TODO
     * models/youtube.ttl

     * models/enola.dev/emoji.ttl:

     * models/enola.dev/term.ttl

     * models/enola.dev/wikipedia.ttl
  -->

Humans [name :snowman: things](https://youtube.com/TODO) <!-- youtube:3 Männer im Schnee, "Der Mensch muss heissen", Kazimir) -->.
Because they are :brain: so smart, they usually know _what_ things they're talking about with these _names_ (often from _"context")._

Computers on the other hand are pretty stupid, and it helps them to have crystal clear unique names of what's what; so Enola uses the following.

## URL

An _"Uniform Resource Locator"_ ([[URL]]) is well-known e.g. as the [[Text]] which you can type into your [[web browsers]]'s address bar. Your browser "fetches" (gets) the [[enola:Resource]] which that URL _"points to"_ - and displays its content to you. Examples of this are:

* <https://google.com> the homepage of the Google Web Search Engine
* <https://google.com?q=Enola.dev> the web page showing the results of a Google search for "Enola.dev"
* <https://www.w3.org/assets/logos/w3c/w3c-no-bars.svg> the logo of the W3C

<!-- TODO Callout with Note: or NOTE: or a different syntax? -->

NOTE: URL are actually a lot less great than one may think at first for really uniquely naming things. For example, <https://google.com> and <https://google.com/> (note the trailing `/` slash) are "the same thing" in practice (that homepage) - as is <https://www.google.com/> (note the `www` prefix), or in some other cases something like `https://www.google.com/index.html`.

URLs have _"schemas"_ - that's the string like `https` - anything before the `:` colon, really. The `https` e.g. means "get it via [[enola:net/HTTP]], and `mailto:` e.g. means "this is an [[enola:email/address]] that you can send an [[Email]] to".

<!-- TODO Move Enola CLI usage examples to a separate linked next page, with same headings? -->

Enola supports URLs, of different schemes; here is how to see which:

```bash
$ ./enola info schemes
http: HyperText Transfer Protocol; see https://enola.dev/net/http (also https:)
file: ...
...
```

Enola can "fetch" the content that an URL points to, like this:

```bash
$ ./enola get http://vorburger.ch/hi
hello, world
```

Getting an URL can raise various errors:

```bash
$ ./enola get http://bad.tld/hi
Error: Could not resolve bad.tld
```

BTW, fun fact: The _#fragment_ (e.g. in `https://server/path#fragment`) of a URL is not relevant for fetching an URL (only e.g. for scrolling to header in displaying); notice how it doesn't change anything here:

```bash
$ ./enola get http://vorburger.ch/hi#there
hello, world
```

## IRI

An _"Internationalized Resource Identifier"_ ([[IRI]) is something fairly different than an [[URL]].

This can, but don't necceessarily really have to,
which can be a bit confusing, at first.

Sometimes can be fetched - but this is not a hard requirement of _Linked Data,_ and so sometimes you cannot.

## URI

An [[URI] ...

## Identity

TBD

### ISBN

TBD

### Other

TBD
