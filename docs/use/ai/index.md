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

    $ ./enola ai --llm=echo:/ --in="hello, world"
    hello, world

    $ ./enola ai --llm="google://?model=gemini-2.5-flash-lite" --in="hello, world"
    Hello, world! How can I help you today?

**TODO** _Document how [this also (already) supports](../../tutorial/agents.md) `--agents`..._
