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

# Agent 🕵🏾‍♀️

_Agents_ use a _[Large Language Model (LLM)](../specs/aiuri/index.md#language-models-lm)_ to reason, plan, and execute actions to achieve a specific goal, often interacting with _[Tools](tool.md)._ They augment LLMs to go beyond just generating text by enabling them to perform tasks and solve problems proactively.

Enola lets you create agents declaratively in YAML or JSON (and maybe TextProto later), based on its [Agent Schema](https://github.com/enola-dev/enola/blob/main/models/enola.dev/ai/agent.schema.yaml).

<!-- TODO Link to tutorial example -->

<!-- TODO Write hand-written documentation about Agent schema... just copy/paste from Schema, for now. -->

<!-- TODO Add a JSON Schema Documentation Generator to Enola, and use it to gen agent.schema.md and link to that from here! -->
