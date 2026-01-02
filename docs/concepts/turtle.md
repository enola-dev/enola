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

<!-- TODO Support YAML frontmatter in all pre-commit checks; the problem is that it's after the license.. :=()
---
id http://www.w3.org/ns/formats/Turtle
enola:mediaType text/turtle
enola:fs/ext ttl
# TODO Rethink tags; they cannot be global, and must be namespaced.
enola:tag ttl
enola:wikipedia: Turtle_(syntax)
# TODO Read this YAML header and display it as MD table on this page on the right when rendered like on Wikipedia!
# TODO Via Wikipedia, find and fetch Wikidata, and load its facts about this Thing, and display (some of) that as well.
---
-->

# RDF Turtle 🐢 Model Language Format Syntax

<!-- TODO Document ^^xsd:base64Binary (et al) Datatype, or language and direction, below... -->

Turtle 🐢 is the _"Terse Resource Description Framework (RDF) Triple Language"_ (TTL).

It's a textual syntax format to write down models of linked things.

This page serves as a brief "cheat sheet" about its syntax.

[The tutorial](../models/example.org/hello.md) elaborates further, incl. alt. formats.

<!-- TODO Write an 'extractor' which pulls ```turtle out of MD, loads, and validates syntax! -->

## Short

The following "full" 3 _Subject - Predicate - Object_ statements:

```turtle
<http://example.org/thing1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Example>.
<http://example.org/thing1> <http://example.org/name> "First Thing".
<http://example.org/thing1> <http://example.org/next> <http://example.org/thing2>.
```

are typically written in this completely equivalent shorter form, with _predicate lists:_

```turtle
<http://example.org/thing1> a <http://example.org/Example>;
  <http://example.org/name> "First Thing";
  <http://example.org/next> <http://example.org/thing2>.
```

## Prefix

<!-- TODO Promote using "PREFIX" instead of "@prefix" to align TTL with SPARQL, and more YAML friendly?
     But it doesn't work; RDF4j probably needs some flag... -->

The initial example above is typically written even shorter, but semantically equivalently, by declaring prefixes, and using [CURIEs](https://en.wikipedia.org/wiki/CURIE) instead:

```turtle
@prefix ex: <http://example.org/>.

ex:thing1 a ex:Example;
  ex:name "First Thing";
  ex:next ex:thing2.
```

There can be several different such prefixes, of course. We can also define (a single one) _default prefix:_

```turtle
@prefix : <http://example.org/>.

:thing1 a :Example;
  :name "First Thing";
  :next :thing2.
```

See [Namespaces](namespaces.md) for more related background.

## Base

Relative instead of absolute IRIs are allowed. By default, they are interpreted as based on "where the TTL is" (e.g. `file:/...`). What you typically want however is to declare an explicit absolute `@base`; e.g. we could also write our example from above like this if we wanted:

```turtle
@base <http://example.org/>.

<thing1> a <Example>;
  <name> "First Thing";
  <next> <thing2>.
```

This variant is valid, and again semantically equivalently to above, but much less commonly used.

## Object Lists

Here is our thing with 2 more names, note the `,` (comma) in the `:name` line:

```turtle
@prefix : <http://example.org/>.

:thing1 a :Example;
  :name "Thing Name", "Another Name";
  :next :thing2.
```

It's important to understand that with this syntax the names are **unordered**.

## Collection

The `(...)` instead of `,` syntax preserves order (and is represented differently internally):

```turtle
@prefix : <http://example.org/>.

:thing1 a :Example;
  :names ("First Name" "Middle Name" "Last Name");
  :next :thing2.
```

## Nest

The `[...]` syntax make this _Thing_ contain another nested _Thing_ (which is "anonymous", and internally represented using a _"blank node"):_

```turtle
@prefix : <http://example.org/>.

:thing1 a :Example;
  :nest [
    :name "Another Thing";
    :next :thing2
  ].
```

<!-- TODO ## Graph

     THIS PROBABLY WON'T BELONG HERE, BUT ELSEWHERE... MAYBE EVEN A NEW "Applications" SECTION?

          Because Enola can extract Turtle from Markdown, we can produce the following view of the above:

          <!--exec:graphviz ../../enola --load docs/concepts/turtle.md rosetta http://example.org/thing1 -o=fd:1?mediaType=text/markdown - ->

          As well as this graph diagram visualization:

          ```graphviz ../../enola --load docs/concepts/turtle.md rosetta -o=fd:1?mediaType=text/vnd.graphviz
          ```
  -->

## References

* [RDF on Wikipedia](https://en.wikipedia.org/wiki/Resource_Description_Framework)
* [Turtle on Wikipedia](https://en.wikipedia.org/wiki/Turtle_(syntax))
* [Turtle RDF W3 Spec](https://www.w3.org/TR/turtle/)
