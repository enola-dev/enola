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

# Classy

You can tell that the `greeting1` and `greeting2` from the previous steps are both some kind of _"Salutations",_ can't you? But Enola cannot.

[`greeting3.ttl`](greeting3.ttl) illustrates how you can make more _"classy"_ greetings:

```turtle
{% include "./greeting3.ttl" start="# limitations under the License.\n" %}
```

This simply expresses that `greeting3` _"is a"_ `Salutation`. We're also adding an Emoji to the _Class,_ which Enola always displays for both that class itself and its instances.

This concludes our introduction about 🐢 Turtle; more detailed information about it is available in [our Turtle Reference documentation](../../concepts/turtle.md).

You can now e.g. re-generate documentation:

```bash cd ../../..
$ ./enola docgen --load docs/models/example.org/greeting3.ttl --output=/tmp/models/ --no-index
...
```

{% include-markdown "./Salutation.md" end="---" comments=false %}

and:

{% include-markdown "./greeting3.md" end="---" comments=false %}

PS: RDF also has (various) ways of expressing "constraints" (AKA "shapes") of _Things,_ based on such _Classes_ (and similar concepts). Enola itself does not directly support e.g. validations of such a _"types"_ (yet) - but you could use other RDF existing tooling!
