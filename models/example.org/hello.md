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

# Hello World

Enola captures knowledge about _Things,_ and the relationships (links) between them.

_Things_ can be represented in a variety of _[Formats](../../concepts/core.md#formats)._ In this 1st step, we'll use the RDF 🐢 Turtle format.

Let's start with this [`greeting1.ttl`](greeting1.ttl):

```turtle
{% include "./greeting1.ttl" start="# limitations under the License.\n" %}
```

This means that `https://example.org/greeting1` identifies some _Thing_ which has a `message` that is _"hello, world"._ Pretty simple, right?

[Among other things](../../use/help/index.md), Enola can _generate documentation_ about _Things,_ like this:

```bash cd .././.././..
$ ./enola docgen --load docs/models/example.org/greeting1.ttl --output=/tmp/models/ --no-index
...
```

[`greeting1.md`](greeting1.md) now contains:

```markdown
{% include-markdown "./greeting1.md" start="# `ex:greeting1`" end="---" comments=false %}
```

Note how the greeting, in addition to our `message` from above, automagically got another _property_ named `origin` - click on it to learn what it's for! Here is how this Markdown renders:

{% include-markdown "./greeting1.md" start="# `ex:greeting1`" end="---" comments=false %}
