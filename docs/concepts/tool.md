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

# Tool 🧰

_Tools_ allow [Agents](agent.md) to access information which is not in a _[Large Language Model (LLM)](../specs/aiuri/index.md#language-models-lm)_ and to interact with their environment.

## Clock 🕰️

```yaml
{% include "../../test/agents/clock.agent.yaml" %}
```

This `clock` tool makes the current date and time available to a model:

```shell
$ enola ai --lm="google://?model=gemini-2.5-flash-lite" --in "What's the time?"
I cannot tell you the current time. I do not have access to real-time information.

$ enola ai --agents=test/agents/clock.agent.yaml --in "What's the time?"
The current date & time in CET is Saturday, August 16, 2025, 11:42 PM.
```

## Exec ▶️

The `exec` tool can be used to run any command; for example, for something like this:

```yaml
{% include "../../test/agents/linux-system-summary.agent.yaml" %}
```

When this Agent is run, it would print (something like) this:

```shell
$ enola ai --agents=test/agents/linux-system-summary.agent.yaml --in="do it"

Xeon CPU runs fast,
Twelve cores, power strong.
Memory 62GiB,
56GiB now in use.
One day, twenty-one mins.
System running well,
Tasks flow with ease.
```

**TODO** _We intended to make this highly configurable in the future._

## Google 🔎 🌐

```yaml
{% include "../../test/agents/google.agent.yaml" %}
```

This `search_google` tool makes the [Google Search Engine](https://search.google/) available:

```shell
$ enola ai --lm="google://?model=gemini-2.5-flash-lite" --in "What happened today?"
As a large language model, I don't have access to real-time information or a concept of "today." My knowledge cutoff is **June 2024**, so I can't tell you what happened today specifically.

$ enola ai --agents=test/agents/google.agent.yaml --in "What happened today?"
Here's a summary of what happened on August 16, 2025:

**International News:**
*   The Indian tricolor was hoisted atop Seattle's iconic Space Needle for the first time, coinciding with India's 79th Independence Day celebrations.

(...)
**Other Notable Events:**
*   August 16th is recognized as National Airborne Day, National Bratwurst Day, National Rum Day, National Roller Coaster Day, and National Tell a Joke Day.
```

This tool is currently only supported [on Gemini](../specs/aiuri/index.md#google-ai-).

## MCP

[MCP](mcp.md) allows Enola to access many thousands of other tools!
