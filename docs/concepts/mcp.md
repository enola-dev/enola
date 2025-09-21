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

[The `--mcp` CLI argument](../use/ai/index.md#mcp) specifies which MCP servers are available to Agents.

If not specified it uses the built-in [`mcp.yaml`](https://github.com/enola-dev/enola/blob/main/models/enola.dev/ai/mcp.yaml) by default.

The `command`, `args` & `env` are self-explanatory; `origin` is just for documentation.

<!-- TODO Document type, url, headers, timeout - once that's tested to work... -->

<!-- TODO Document input, once that's implemented... -->

The boolean `roots` flag controls whether the current working directory is exposed; it defaults to false.

The `log` field controls the logging level of the MCP server, and can be set to `debug`, `info`, `notice`, `warning`, `error`, `critical`, `alert` and `emergency`. If unspecified, it defaults to the `warning` level. This only controls what the MCP server sends. To actually see all log messages on the client, you must [start Enola with `-vvvvv`](../use/log/index.md).

Use the names under the `servers:` key of a `mcp.yaml` in the `tools:` of [Agents](agent.md).

MCP servers are only started (or connected to), and queried for their 🧰 Tools, if any of the loaded `--agents` use them.

<!--
    ## Recommended

    TODO

    Zapier
    Google Mail & Calendar & Drive!
    RAG with Pinecone, LlamaIndex?
    OpenAPI (HF)?
-->

## Examples

<!-- Generate these, from an example prompt in YAML... -->

### Fetch

```yaml
{% include "../../test/agents/fetch.agent.yaml" %}
```

The [`fetch` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/fetch) can fetch a webpage, and extract its contents as Markdown:

```shell
enola ai -a test/agents/fetch.agent.yaml --in="What is on https://docs.enola.dev/tutorial/agents/ ?"
```

This needs `uvx` to be available; test if launching `uvx mcp-server-fetch` works, first.

CAUTION: This server can access local/internal IP addresses, which may represent a security risk.
Exercise caution when using this MCP server to ensure this does not expose any sensitive data!

### Git

```yaml
{% include "../../test/agents/git.agent.yaml" %}
```

```shell
enola ai --agents=test/agents/git.agent.yaml --in "Write a proposed commit message for the uncommitted files in $PWD"
```

CAUTION: This server is inherently insecure; you should carefully evaluate if it meets your needs.

This needs `uvx` to be available; test if launching `uvx mcp-server-git` works, first.

### Memory

```yaml
{% include "../../test/agents/memory.agent.yaml" %}
```

[Memory](https://github.com/modelcontextprotocol/servers/tree/main/src/memory) can remember things:

```shell
$ enola -vv ai --agents=test/agents/memory.agent.yaml --in "John Smith is a person who speaks fluent Spanish."
I have noted that John Smith is a person who speaks fluent Spanish.
```

`cat ~/memory.json` let's you see the memory 🧠 cells! 😝 Now, perhaps another day:

```shell
$ enola -v ai --agents=test/agents/memory.agent.yaml --in "Does John Smith speak Italian?"
Remembering...Based on my memory, John Smith speaks fluent Spanish. I do not have any information indicating that he speaks Italian.
```

This needs `npx` to be available; test if launching `npx @modelcontextprotocol/server-memory` works, first.

### Everything

The [`everything` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/everything) has a number of tools useful for debugging and testing the MCP protocol:

```shell
enola ai --agents=test/agents/everything.agent.yaml --in "Print environment variables to debug MCP"
```

## CLI for Debugging

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
