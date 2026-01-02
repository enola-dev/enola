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

# XML

Enola can read Things from XML (any, non-RDF!); see [Rosetta](../use/rosetta/index.md#xml).

This _"direct"_ XML support is different from its ["RDF XML"](xml-rdf.md) support.

The [Tika](tika.md) multi-format I/O internally uses this XML support.

Writing Things converted to XML is still TBD.

## Mapping

## Attributes & Elements

Both XML _attributes_ and _elements_ are converted to Thing properties / RDF statements.

## Text

Text content from XML may be directly used as string objects of Things' property / RDF statements.

If this is not possible, then it is wrapped in `https://enola.dev/text` properties.

This depends on whether there are other elements.

## Namespaces

[Namespaces](namespaces.md) of XML are supported, both default and with prefixes.

If no `xmlns` is set, the resource's URI is used as fallback instead.

As XML default namespaces do not apply to attributes, they must use a prefix.

### Empty

An XML input consisting only of an empty `<root/>` like element is considered "empty" (no Things).

An XML input without any bytes is valid, and equally simply gets converted to _No Things._
