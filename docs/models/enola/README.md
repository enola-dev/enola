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

# Enola Built-In Types

<!-- ExecMD: enola.types.yaml type metadata should be inserted into a doc gen. from this one! -->

## URL

An _URL_ is a _Uniform Resource Locator._ That sounds complicated,
but it's actually something everyone is very familiar with nowadays:

It's simply that text which you type into your web browser's address bar, on top!
There is also (typically) one of these underlying what click on in a web page.

For example, https://docs.enola.dev or https://www.google.com/search?q=enola.dev are both URLs.

Most URLs start with `https:`, but there are also e.g. `file:` or `mailto:`, and some others; including [Enola's own `enola:`](https://docs.enola.dev/concepts/uri/).

See also https://en.wikipedia.org/wiki/URL and https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/What_is_a_URL.

## GUN

A _GUN_ is a _Globally Unique Name._ This is an Enola.dev invented term, not an industry standard.

It's simply a `string` that you are "fairly sure" is unique. For public Enola instances, an easy way to "guarantee" such uniqueness is the "convention" to start it with a domain name which you own, either in the format used in URLs (e.g. `enola.dev`) or the "reverse notation" (e.g. `dev.enola`) which is more popular in some environments (e.g. Java or Proto packages).

Some GUNs may be URIs or even URLs, but this not specifically required. More technically, a GUN does NOT (necessarily) have a _scheme, path, authority, query, fragment_ structure. So "your.org/something" is a "valid GUN", as is really any string, but it's technically not a valid URL (because it has no scheme), but could be a valid (relative) URI - but that's a "coincidence".

On private networks e.g. in internal corporate intranet deployments, a GUN can really be any string that you are comfortable with being unique within that Enola instance.

## ID

An _ID_ in Enola's context is simply a sequence of bytes.

It is sometimes used internally as permanent technical identifier which may be shorter than e.g. a Type's GUN, or another entity name or path or such.

The length is not fixed, but could be e.g. 16 bytes (128 bits) to represent an [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier); this should however never be relied upon.

Binary Enola IDs can have various textual representations; for example, as UUID text (if it's 16 bytes, or less), or as a [Multiformats Multibase](https://multiformats.io), or any other such encoding.

## MLS

A _MLS_ in Enola is simply a string in multiple languages.

Technically it's a `Map` where the keys are [IETF BCP 47](https://www.rfc-editor.org/info/bcp47) "language codes" (like e.g. `en` or `de-CH`) and the value is text in that language.

<!-- Consider using MIME "multipart/multilingual" https://www.rfc-editor.org/rfc/rfc8255.html -->

## Email

Email is what you know, an electronic message; here specifically an address you can send one to.

## Proto

In Enola's context, "proto" refers to https://protobuf.dev.

Specifically, this type is a particular `message` structure.
