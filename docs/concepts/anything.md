<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025 The Enola <https://enola.dev> Authors

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

# Philosophy of Anything

Original Author: [Michael Vorburger.ch](https://www.vorburger.ch/)

<!-- Replace all Wikipedia links here with https://enola.dev/... equivalents? -->

## Words

When you say _gift_ then I am not entirely sure what you mean...
probably a 🎁 - or did you maybe refer to the German word _Gift,_
which is 💀 poison in English?

In our everyday natural language, there is often _context_ which helps us to disambiguate.
But how could I (or a computer) **really** know what you meant? Well, we'll just provide context!

So to make it clearer (and even unambiguous), we just specify a _Type_ for context.
For example, we could say _the English word gift_ (or alternatively _the German word Gift_ instead).

More formally, if you gave me (an of _[ordered pair](https://en.wikipedia.org/wiki/Ordered_pair)_ of)
both _"gift"_ and _English_ then I can be certain that you mean a 🎁 and are not giving me 💀.

## Datatype

## Identifier

When you say _0-13-140731-7,_ what do you mean? I'm again not really sure, and it could refer to a number of different _Things._

Perhaps you meant it as [an _International Standard Book Number_ (ISBN)](https://en.wikipedia.org/wiki/ISBN), to _identify_ the [Core Java Data Objects](https://www.vorburger.ch/corejdo/) book?

maybe... "Intelligent Systems for Biological Nomenclature"

As above
So _0-13-140731-7,_

## IRI

https://www.dév.dev

do not "DIY" (AKA "Do Identifiers by Yourself")

## URL

mention URI

https://www.xn--dv-bja.dev

## Literals

when you say _1_ probably a number (1)

Similarly, you could say _the Integer 1._ But now what exactly is an _Integer..._
the [mathematical](https://en.wikipedia.org/wiki/Integer)_ (unbounded), or
some _[technical](https://en.wikipedia.org/wiki/Integer_(computer_science))_ (32bit? 64bit? un-/signed?) one?

Again, we need some way to uniquely identify these different types.
You could probably easily think of a few different ways to identify something uniquely?
For example, we could make up long numbers that we both agreed upon beforehand,
and figure out some way to keep them unique in the world.
But this would be tedious.

Another way would be to use some sort of "uniquely identifying name" in text form.
For example, `integer-math-unbounded` vs `integer-tech-32bit-signed`.

as long as we both agreed on this.

Added advantage that you could type that into your web browser.

URL

namespace prefix

Literal
Datatype

http://www.w3.org/2001/XMLSchema#integer

lang `en`or `de`
with IRI

"structure"

amount: 7
item: GIFT

=

:

format

JSON

YAML, with !

Thing

0s and 1s

MediaType

<!-- TODO ## sameAs

Now, assume that for some hypothetical reason I wanted to know whether

https://en.wikipedia.org/wiki/Swiss_Standard_German

```turtle
"Billett"^^lang:de-CH owl:sameAs "Ticket"^^lang:de.

"Gschenkli"^^lang:de-gsw

-->

<!-- TODO Mermaid diagram illustrating the relationship of all the concepts introduced above... -->

<!-- TODO Replace Mermaid diagram with Enola Network Graph with links to models -->

## References

While this succinct write-up is novel, the underlying ideas presented herein are old and well-established, and based e.g. on:

* [Schema.org](https://schema.org/)
* [Resource Description Framework](https://en.wikipedia.org/wiki/Resource_Description_Framework)
* [Frame problem](https://en.wikipedia.org/wiki/Frame_problem)
* [10 Simple rules for design, provision, and reuse of persistent identifiers for life science data](https://zenodo.org/record/18003/files/MS_2015-05-23.pdf)

<!-- TODO Add more reference links? -->
