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

# AI Chat 💬 Tutorial

You can interact with Enola's AI using any of the following [commands](../use/help/index.md).

They all have an `--agents` argument to configure [AI Agents](agents.md).

## Web UI Server

For the simplest possible initial demo, try to just launch:

    docker pull ghcr.io/enola-dev/enola:main
    docker run --rm --volume "$PWD":/app/CWD/:Z --tty -p7070:7070 ghcr.io/enola-dev/enola:main \
        server --chatPort=7070 --lm=echo:/

This will give you a Chat web UI on <http://localhost:7070> which will simply echo everything you write back to you, for starters.

Of course, next this also works with [real AI Language Models](../specs/aiuri/index.md#language-models-lm); for more details, see [`server`](../use/server/index.md#chat).

## Terminal (TUI)

If you prefer using the terminal instead of a Web UI, then alternatively just launch:

    ./enola chat

<!-- TODO --lm=echo:/ -->

For more details, see [`chat`](../use/chat/index.md#ai).

## AI CLI

See [`ai` command](../use/ai/index.md).
