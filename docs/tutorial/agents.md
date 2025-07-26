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

# Agents 🕵🏾‍♀️ Tutorial

[AI Agents](../concepts/agent.md) are _the most awesome thing since life spread!_

Agents work in [all Chat UXs of Enola](chat.md). Here are some examples to use them.

<!-- TODO Replace ./enola with container, and test/agents with http://github.com ... raw ... -->

## Optimistic Chef 👨🏽‍🍳

```yaml
{% include "../../test/agents/chef-optimist.agent.yaml" %}
```

can be used like this to chat with a very optimistic 👨🏽‍🍳 chef:

    ./enola server --lm="google://?model=gemini-2.5-flash-lite" \
      --agents=test/agents/chef-optimist.agent.yaml --chatPort=7070

## Cynical Chef 😾

```yaml
{% include "../../test/agents/chefs-opposites-stream.agent.yaml" %}
```

can be used like this:

    ./enola server --lm="google://?model=gemini-2.5-flash-lite" \
      --agents=test/agents/chef-optimist.agent.yaml --chatPort=7070

The drop-down in the Web UI will let you select the `optimist` _vs._ the `pessimist` chef agent.
