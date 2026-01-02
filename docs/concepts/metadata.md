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

# Metadata

Enola uses Metadata about all of its _Things_ when rendering them:

<!-- This describes the dev.enola.common.io.metadata.Metadata record; keep it updated, if it ever changes. -->

* label is a normally fairly short human-readable 🏷️ text; e.g. TITLE of HTML page found at the IRI, or something similar.
* description is a longer human-readable 📜 description; e.g. first paragraph or `<meta ... description>` of a HTML page.
* image of a logo; e.g. from [favicon](https://de.wikipedia.org/wiki/Favicon) or something like that; or an 😃 Emoji!
* curie is a compact URI; a shorter form of the original "long" IRI of the Thing.

[The `info metadata` sub-command](../use/info/index.md#metadata) is a handy tool to test this.

<!-- The following documents the dev.enola.thing.metadata.ThingMetadataProvider service; keep it updated, if it ever changes. -->

## Label

This is read from a number of "typical" properties; in order of priority:

1. https://enola.dev/label
1. [http://www.w3.org/2000/01/rdf-schema#label](../models/www.w3.org/2000/01/rdf-schema/label.md)
1. https://schema.org/name
1. http://purl.org/dc/elements/1.1/title

If the thing itself doesn't have any of these, it will check if its [RDFS range](../models/www.w3.org/2000/01/rdf-schema/range.md) has any.

Alternatively, the (RDFS) `Class` of a Thing's (RDF) `type` can specify an [https://enola.dev/labelProperty](../models/enola.dev/labelProperty.md)
to specify the IRI of a "custom" property to use a label, if present; see `test/metadata-label-property.ttl` for an example illustrating how to use this.

This is always available; if a Thing has none of the above, then it will fallback to the CURIE, or else just the last part of its IRI.

## Description

This is also read from a number of "typical" properties; in order of priority:

1. https://enola.dev/description
1. https://schema.org/description
1. https://schema.org/abstract
1. http://purl.org/dc/elements/1.1/description
1. [http://www.w3.org/2000/01/rdf-schema#comment](../models/www.w3.org/2000/01/rdf-schema/comment.md)

This is optional and may be empty.

## Image of Logo

This is read, again in order, from:

1. [https://enola.dev/emoji](../models/enola.dev/emoji.md)
1. https://schema.org/logo
1. https://schema.org/thumbnailUrl
1. https://schema.org/image

and similarly to above first on the Thing itself, or if none then via its [RDFS range](../models/www.w3.org/2000/01/rdf-schema/range.md) or
[RDF type](../models/www.w3.org/1999/02/22-rdf-syntax-ns/type.md).

## CURIE

The CURIE is determined by the current context.

This could be different than e.g. a prefix in a loaded [Turtle](turtle.md).
