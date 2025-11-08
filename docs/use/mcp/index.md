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

`mcp` has useful commands for exploring [MCP Tools](../../concepts/mcp.md).

This is similar to the [MCP Inspector](https://github.com/modelcontextprotocol/inspector), and other such tools.

## Call MCP Tool

```bash cd ../.././..
$ ./enola -v mcp call-tool modelcontextprotocol/everything echo '{"message":"hi"}'
...
```

## List MCP Tools

```bash $% cd ../.././..
$ ./enola mcp list-tools --help
...
```

<!-- TODO Why does this not work?! Is it just because secrets are missing on CI? See also test-cli.bash ...

```bash cd ../.././..
$ ./enola -vv mcp list-tools
...
```
--->
