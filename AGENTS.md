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

# AI Agents Conventions

This project can be developed and maintained by AI agents, such as https://enola.dev itself, or the Gemini CLI or Claude Code or Copilot etc.

This https://agents.md file documents conventions which agentic AI tools must follow when working on this project.

## Development Process

After making any changes to the codebase, you MUST run the full test suite to verify that the requested goal was achieved and that you did not accidentally break anything else in the process. To run the tests, execute the following command from the root of the project:

```bash
./test.bash
```

## Terminology

* MCP stands for Model Context Protocol, see https://modelcontextprotocol.io
