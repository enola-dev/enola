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

# MCP 🔱

The 🔱 [Model Context Protocol](https://modelcontextprotocol.io) (MCP) is a standard for 🧰 [Tools](tool.md).

## Configuration

The `--mcp` CLI argument specifies which MCP servers are available to Agents.

If not specified it uses the built-in [`mcp.yaml`](https://github.com/enola-dev/enola/blob/main/models/enola.dev/ai/mcp.yaml) by default.

Use the names under the `servers:` key of a `mcp.yaml` in the `tools:` of [Agents](agent.md).

MCP servers are only started (or connected to), and queried for their 🧰 Tools, if any of the loaded `--agents` use them.

<!--
    ## Recommended

    TODO

    Filesystem!
    Memory...
        https://github.com/modelcontextprotocol/servers/tree/main/src/memory
    Zapier
    Google Mail & Calendar & Drive!
    RAG with Pinecone, LlamaIndex?
    OpenAPI (HF)?
-->

## Examples

<!-- Generate these, from an example prompt in YAML... -->

### Everything

The [`everything` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/everything) has a number of tools useful for testing:

```shell
enola ai --agents=test/agents/everything.agent.yaml --in "Print environment variables to debug MCP"
```

### Fetch

The [`fetch` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/fetch) can fetch a webpage, and extract its contents as Markdown:

```shell
enola ai -a test/agents/fetch.agent.yaml --in="What is on https://docs.enola.dev/tutorial/agents/ ?"
```

CAUTION: This server can access local/internal IP addresses, which may represent a security risk.
Exercise caution when using this MCP server to ensure this does not expose any sensitive data!

## CLI

To debug MCP, use the dedicated [MCP CLI commands](../use/mcp/index.md).

## Directories

<!-- TODO Crawl these, and gen. MD pushed to https://github.com/enola-dev/awesome-mcp ... -->

* https://glama.ai/mcp/servers == https://github.com/punkpeye/awesome-mcp-servers
* https://github.com/wong2/awesome-mcp-servers
* https://hub.docker.com/mcp
* https://mcp.so == https://github.com/chatmcp/mcpso
* https://mcpservers.org
* https://www.mcp.run/registry
* https://cursor.directory/mcp
* https://cline.bot/mcp-marketplace
* https://www.claudemcp.com/servers
* https://www.pulsemcp.com/servers
* https://smithery.ai
* https://mcpmarket.com
* https://www.awesomemcp.com
* https://www.mcpserverfinder.com
* https://mcp.higress.ai
* https://github.com/appcypher/awesome-mcp-servers
* https://github.com/pipedreamhq/awesome-mcp-servers
* https://github.com/MobinX/awesome-mcp-list
* https://github.com/toolsdk-ai/awesome-mcp-registry
* https://github.com/modelcontextprotocol/servers

<!-- TODO https://github.com/andrew/ultimate-awesome "MCP" -->
