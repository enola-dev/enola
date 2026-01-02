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

# Templates

Are you bored with `greeting1`, `greeting2` & `greeting3` by now? 😼 Fear not - Enola has a way to define **ALL** _Greetings!_ 😸 To learn how, check out [`greetingN.ttl`](greetingN.ttl):

```turtle
{% include "./greetingN.ttl" start="# limitations under the License.\n" %}
```

This means:

1. All `https://example.org/greet/{NUMBER}`, such as `https://example.org/greet/456` as well as `https://example.org/greet/789` (and any other) _ARE_ a `Greeting`. (See also [the `iriTemplate` property documentation](../enola.dev/iriTemplate.md).)

1. The `yo` _property_ says that **all** _Greetings_ (not just the above class!) have a link - but it depends on the `NUMBER` from the IRI; e.g. `https://example.org/greet/456` has a `yo` link to `http://example.org/hi/456` but `https://example.org/greet/789` to `http://example.org/hi/789`.

1. `https://example.org/greet/42` is one an example of such a _Greeting._

Check it out by generating documentation:

```bash cd .././.././..
$ ./enola docgen --load docs/models/example.org/greetingN.ttl --output=/tmp/models/ --no-index
...
```

{% include-markdown "./greeting.md" start="# `ex:greeting`" end="---" comments=false %}

Now click on the `example` link (42)... can you tell what happened?! 😻

PS: In Technical Infrastructure Models, _Things_ like a _Machine,_ or perhaps more ephemeral concepts such _Kubernetes Pod_ might be good candidates for such _"templated Things"._
