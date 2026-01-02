<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

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

# Enola Resource Identifier URIs (ERIs)

??? warning

    _This page needs some updates to fully match the current code._

## Introduction

Enola's [CLI](../use/help/index.md) (and [Web UI](../use/server/index.md))
accept an argument which uniquely identifies the [_Entity_](core-arch.md) to
_GET,_ _LIST_ or otherwise act upon.

This is a
_[Uniform Resource Identifier](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier)_
(URI), conceptually somewhat similar to the URL address of this documentation
site in your web browser.

Enola originally referred to this concept as an **`ID`**, and some of this
documentation (and code) currently still does. The intention is to gradually
completely remove the ID terminology and exclusively use _Enola Entity URI;_
this page documents both what of this is currently available, and what is
planned for the future.

## Available

An Enola URI was originally defined as an `enola:{ns}.{entity}/path1/path2/...` ID; for example:
`enola:demo.foo/abc/def`.

This original `{ns}.{entity}/paths...` syntax has meanwhile been loosened to permit any unique string.

The `enola:` URI scheme prefix is often omitted when an URI is not in the full
normal form described on this page. Within Enola, the Enola CLI and Web UI
accept it, but do not require it; so `demo.foo/abc/def` is valid, and fully
equivalent. (This URI scheme is not currently
[registered with the IANA](https://en.m.wikipedia.org/wiki/List_of_URI_schemes).)

Both the `enola.dev` and `enola` namespaces are reserved for internal use by models built-in to this
project, e.g. `enola.entity_kind` and `enola.schema`, and should not be used in
user models. <!-- TODO Later change these to be enola.dev/schema etc. -->

Enola alternatively also accepts a
[_Uniform Resource Name_](https://en.wikipedia.org/wiki/Uniform_Resource_Name)
syntax; e.g. `urn:enola:demo.foo/abc/def`.

Other schemes than `enola:` or `urn:enola` are assumed to point to a Resource,
and attempted to be fetched and [converted](../use/rosetta/index.md) to a Thing.
<!-- TODO Link to doc with more explanation about Things. -->

The `?query` and `#fragment` of an URI are not supported, and will be rejected.

The Java classes `dev.enola.core.ERI` and `dev.enola.core.IDs` implement this logic,
but they are an Enola internal implementation detail, not an API intended for users.

## TODO Planned Future Extensions

Some commands accept
[a `--server` argument](https://docs.enola.dev/use/help/#get). The plan is to
optionally allow to include that server endpoint in this URI, as it's _"authority"_ component. Endpoints will not
be limited to traditional IP address and DNS hostname resolution, only, but
could include other protocols to connect to another Enola server, for federation.

## Internationalization

The project envisions to eventually fully support
_[Internationalized Resource Identifiers](https://en.wikipedia.org/wiki/Internationalized_Resource_Identifier)_
(IRIs), instead of only ASCII URI syntax; more testing to identify and fix any
related gaps is required.
