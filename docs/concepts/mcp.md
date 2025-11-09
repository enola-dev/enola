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

For STDIO, the `command`, `args` & `env` are self-explanatory.

For Streamable HTTP (Chunked Transfer), set `url:` - and possibly `headers:` e.g. with `Authorization: Bearer ${secret:XYZ}`. For Server-Sent Events (SSE), use `type: sse`.

The `timeout` specifies Timeout duration, for both STDIO & HTTP; it defaults to _7 seconds._

The `docs` field is for a URL to link to documentation.

The boolean `roots` flag controls whether the current working directory is exposed; it defaults to false.

The `log` field controls the logging level of the MCP server, and can be set to `debug`, `info`, `notice`, `warning`, `error`, `critical`, `alert` and `emergency`. If unspecified, it defaults to the `warning` level. This only controls what the MCP server sends. To actually see all log messages on the client, you must [start Enola with `-vvvvv`](../use/log/index.md).

Use the names under the `servers:` key of a `mcp.yaml` in the `tools:` of [Agents](agent.md).

MCP servers are only started (or connected to), and queried for their 🧰 Tools, if any of the loaded `--agents` use them.

### Secrets

Enola will replace values of `${secret:XYZ}` with the [secret](../use/secret/index.md) named `XYZ` in `args`, `env` and `headers`.

## Examples

<!--
    ## Recommended

    TODO

    Zapier
    Google Mail & Drive!
    RAG with Pinecone, LlamaIndex?
    OpenAPI (HF)?
-->

<!-- TODO Generate all of this, from an example prompt in mcp.yaml... -->

### Fetch

```yaml
{% include "../../test/agents/fetch.agent.yaml" %}
```

The [`fetch` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/fetch) can fetch a webpage, and extract its contents as Markdown:

```shell
enola ai -a test/agents/fetch.agent.yaml --prompt="What is on https://docs.enola.dev/tutorial/agents/ ?"
```

This needs `uvx` to be available; test if launching `uvx mcp-server-fetch` works, first.

CAUTION: This server can access local/internal IP addresses, which may represent a security risk.
Exercise caution when using this MCP server to ensure this does not expose any sensitive data!

### Brave

Create a [secret](../use/secret/index.md) named `BRAVE_API_KEY` with a [Brave Search API Key](https://api-dashboard.search.brave.com/app/keys).

```yaml
{% include "../../test/agents/brave.agent.yaml" %}
```

The [`search-brave`](https://github.com/brave/brave-search-mcp-server) tool uses <https://search.brave.com>
(via its [API](https://brave.com/search/api/)) for Web / Local / Video / Image / News Searches; for example:

```shell
enola ai --agents=test/agents/brave.agent.yaml --prompt="Use the brave_news_search tool to obtain what's new in world politics today and summarize the top 7 developments in bullet points of maximum 3 sentences each."
```

### Google Calendar

Follow https://github.com/nspady/google-calendar-mcp#quick-start to obtain the GCP OAuth Key JSON file.

Edit `test/mcp/google-calendar.yaml` and set `GOOGLE_OAUTH_CREDENTIALS` to the path of this JSON file. Now run:

```shell
./enola -vvvv mcp list-tools --mcp=test/mcp/google-calendar.yaml
```

This will open a web-browser, where you need to authorize access to your Google account.
The token from this authorization will be saved to `~/.config/google-calendar-mcp/tokens.json`.
Re-run `mcp list-tools` and make sure that it prints the calendar MCP tools.
If that's successful, then you can use it e.g. like this:

```shell
./enola ai --mcp=test/mcp/google-calendar.yaml --agents=test/agents/google-calendar.agent.yaml --prompt="What upcoming meetings do I have scheduled?"
```

TODO Contribute an improvement to `nspady/google-calendar-mcp` so that JSON can be provided directly via
an environment variable or argument (secret), instead of via file; then simplify above,
and move `test/mcp/google-calendar.yaml` to `models/enola.dev/ai/mcp.yaml`.

### Git

```yaml
{% include "../../test/agents/git.agent.yaml" %}
```

```shell
enola ai --agents=test/agents/git.agent.yaml --prompt "Write a proposed commit message for the uncommitted files in $PWD"
```

CAUTION: This server is inherently insecure; you should carefully evaluate if it meets your needs.

This needs `uvx` to be available; test if launching `uvx mcp-server-git` works, first.

### GitHub

```yaml
{% include "../../test/agents/github.agent.yaml" %}
```

Create a [secret](../use/secret/index.md) named `GITHUB_PAT` [GitHub Personal Access Token](https://github.com/settings/personal-access-tokens/new).

```shell
enola ai --agents=test/agents/github.agent.yaml --prompt "How many stars do the top 3 repos that I own on GitHub repo have? (Use the GitHub context tool to find by GitHub user name.)"
```

### Memory

```yaml
{% include "../../test/agents/memory.agent.yaml" %}
```

[Memory](https://github.com/modelcontextprotocol/servers/tree/main/src/memory) can remember things:

```shell
$ enola -vv ai --agents=test/agents/memory.agent.yaml --prompt "John Smith is a person who speaks fluent Spanish."
I have noted that John Smith is a person who speaks fluent Spanish.
```

`cat ~/memory.json` let's you see the memory 🧠 cells! 😝 Now, perhaps another day:

```shell
$ enola -v ai --agents=test/agents/memory.agent.yaml --prompt "Does John Smith speak Italian?"
Remembering...Based on my memory, John Smith speaks fluent Spanish. I do not have any information indicating that he speaks Italian.
```

This needs `npx` to be available; test if launching `npx @modelcontextprotocol/server-memory` works, first.

### Everything

The [`everything` MCP server](https://github.com/modelcontextprotocol/servers/tree/main/src/everything) has a number of tools useful for debugging and testing the MCP protocol:

```shell
enola ai --agents=test/agents/everything.agent.yaml --prompt "Print environment variables to debug MCP"
```

## CLI for Debugging

To debug MCP, use the dedicated [MCP CLI commands](../use/mcp/index.md).

## Directories

[Browsers](https://github.com/modelcontextprotocol/registry/blob/main/docs/community-projects.md) for the [Registry](https://github.com/modelcontextprotocol/registry/):

<!-- TODO Crawl these, and gen. MD pushed to https://github.com/enola-dev/awesome-mcp ... -->

* **[MCP Bench](https://mcpbench.ai/)**
* [TeamSpark MCP Server Discovery](https://teamsparkai.github.io/ToolCatalog/)
* [Datasette](https://lite.datasette.io/?url=https%3A%2F%2Fraw.githubusercontent.com%2Frosmur%2Fofficial-mcp-registry-database%2Fmain%2Fofficial_mcp_registry.db#/official_mcp_registry/servers)

Other:

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
