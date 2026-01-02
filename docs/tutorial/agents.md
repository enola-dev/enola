<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025-2026 The Enola <https://enola.dev> Authors

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

This tutorial uses Enola from a [container](../use/index.md#container), but there are other ways to [install Enola](../use/index.md).

<!-- TODO Use a new builtin: URL scheme instead of http://github.com URLs! -->

## Optimistic Chef 👨🏽‍🍳

```yaml
{% include "../../test/agents/chef-optimist.agent.yaml" %}
```

can be used like this to chat with a very enthusiastic 👨🏽‍🍳 chef:

    docker run --rm --volume "$PWD":/app/CWD/:Z --tty -p7070:7070 \
      -e GOOGLE_AI_API_KEY=... ghcr.io/enola-dev/enola:main \
      server --chatPort=7070 --lm="google://?model=gemini-2.5-flash" \
      --http-scheme --agents=https://raw.githubusercontent.com/enola-dev/enola/refs/heads/main/test/agents/chef-optimist.agent.yaml

[See here](../specs/aiuri/index.md#google-ai) re. `GOOGLE_AI_API_KEY` etc. and now open <http://localhost:7070> to open the UI.

## Cynical Chef 😾

```yaml
{% include "../../test/agents/chefs-opposites-stream.agent.yaml" %}
```

can be used by replacing `chef-optimist.agent.yaml` with `chefs-opposites-stream.agent.yaml` in the command above.

The drop-down in the Web UI will now let you select the `optimist` _vs._ the `pessimist` chef agent.

## Tools

Agents become a lot more powerful [with Tools](../concepts/tool.md).
