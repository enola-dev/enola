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

# Agent 🕵🏾‍♀️ Concept

_Agents_ use a _[Large Language Model (LLM)](../specs/aiuri/index.md#language-models-lm)_ to reason, plan, and execute actions to achieve a specific goal, often interacting with _[Tools](tool.md)._ They augment LLMs to go beyond just generating text by enabling them to perform tasks and solve problems proactively.

Enola lets you easily create ‘opinionated’ (“only one way to do it”) _No Code_ agents
declaratively in YAML or JSON (and maybe TextProto later), based on its [Agent Schema](https://github.com/enola-dev/enola/blob/main/models/enola.dev/ai/agent.schema.yaml).

**[Check out the tutorial](../tutorial/agents.md)!**

_[Check out our existing agents!](../agents/index.md)_

## Schema

<!-- NB: This is copy/pasted from agent.schema.yaml; please keep them (manually, for now) in sync! TODO Add a JSON Schema Documentation Generator to Enola, and use it to gen agent.schema.md and link to that from here... -->

See https://github.com/enola-dev/enola/tree/main/test/agents for examples.

### Instruction

Instructions for LLM model, guiding the agent's behaviour. You should describe concisely what the agent will do, when it should defer to other agents/tools, and how it should respond to the user.

### Description

One-line description of the agent's capability. The model uses this to determine whether to delegate control to the agent.

### Model

The language model to use, based on the [Enola.dev AI URI specification](../specs/aiuri/index.md); so e.g. `google://?model=gemini-2.5-flash`. May be omitted, in which case a default one (built-in, or specified as a parameter to a CLI) will typically be used.

### Name

Name ("nick") of agent. Typically, it's set automatically by a loader from a portion of the origin URL. <!-- This is just a recommendation, and a loader could change it if another agent (with another ID) already uses this name. Users can also change the nicknames of their agents. -->

### Output Schema

The `output.schema` allows specifying the expected structure of the output from the agent in JSON Schema.

The [`person.agent.yaml`](https://github.com/enola-dev/enola/blob/main/test/agents/person.agent.yaml) example illustrates how to use this.

### Tools

[Tools](tool.md) to which the agent has access, including [MCP](mcp.md).

## TODO

_Agents will soon allow configuring parallel (or sequential) and "looped" execution of other agents!_
