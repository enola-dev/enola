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

# AI 🔮 Command

The `ai` command [works with Agents](../../tutorial/chat.md) with input from the CLI:

    $ enola ai --llm=echo:/ --prompt="hello, world"
    hello, world

    $ enola ai --llm="google://?model=gemini-2.5-flash-lite" --prompt="hello, world"
    Hello, world! How can I help you today?

You, of course, also use the [Chat Web UI](../server/index.md#chat) or the [Console Chat](../chat/index.md) to interact with Agents.

All of these 3 commands (`ai`, `chat`, and `server`) support the following CLI flags / options.

## Agents

`--agents` loads [AI Agents](../../concepts/agent.md). It is possible to specify multiple agents by repeating the flag.

If a "short name" (`[a-zA-Z0-9\-]+`) is given, e.g. [`--agents=weather`](../../agents/weather.md), then this is implicitly
mapped to `https://raw.githubusercontent.com/enola-dev/$NAME-agent/refs/heads/main/enola.agent.yaml`.

Otherwise, it loads the agent definition from the given local file (e.g. `--agents=dir/example.yaml`)
or [fetches](../fetch/index.md) a non-file remote URL (e.g. `--agents=https://example.com/agent.yaml`).

See the [tutorial](../../tutorial/agents.md) for usage examples.

## Default Agent

`--default-agent` specifies which of the loaded [AI Agents](../../concepts/agent.md) to use if none is otherwise selected.

## LLM

`--lm` needs to be a valid [AI LM URI](../../specs/aiuri/index.md).

It is optional, because [Agents can set this via `model:` as well](../../concepts/agent.md#model).

## Prompt

`--prompt` is the input prompt to the LLM / Agent.

## Attach

`--attach` allows attaching files to the LLM / Agent prompt. It can be repeated to attach multiple files (e.g. `--attach=image.png --attach=document.pdf`).

The files are referenced by URL, and support relative URLs for local files and fetching remote URLs with [various schemes](../fetch/index.md#schemes).

Example with image attachment:

    $ enola ai --llm="google://?model=gemini-2.5-flash-lite" \
        --prompt="What do you see in this image?" \
        --attach=test/mystery.jpeg

<!-- The mystery.png is from, and an homage to, my https://github.com/vorburger/SimpleHTTPServer/blob/master/src/test/resources/devdog.jpg from http://blog1.vorburger.ch/2006/06/simple-http-server-in-java.html -->

Supported image formats, understanding, token cost, maximum number of images per request and their maximum sizes obviously depend on the LLM used:

* [Gemini](https://ai.google.dev/gemini-api/docs/image-understanding)

## MCP

`--mcp` enables [MCP](../../concepts/mcp.md#configuration) for the Agent(s).
