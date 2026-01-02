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

# Linked Data

Now check out the [`greeting2.ttl`](greeting2.ttl):

```turtle
{% include "./greeting2.ttl" start="# limitations under the License.\n" %}
```

Let's generate documentation:

```bash cd .././.././..
$ ./enola docgen --load docs/models/example.org/greeting2.ttl --output=/tmp/models/ --no-index
...
```

[`greeting2.md`](greeting2.md) now contains:

```markdown
{% include-markdown "./greeting2.md" start="# `ex:greeting2`" end="---" comments=false %}
```

There are a couple of things worth noting here:

1. We've introduced the `@prefix` shortcut, just to avoid repeating `https://example.org`.
1. Instead of a `hello, world` string, we're now greeting an _Object,_ the `https://example.org/world` - this is what _Linked Data_ is all about!
1. That `world` object contains yet another link, but this one is different... can you tell how & why, by clicking on `world` in the rendered Markdown below?

{% include-markdown "./greeting2.md" start="# `ex:greeting2`" end="---" comments=false %}
