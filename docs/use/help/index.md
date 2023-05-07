<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

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

# Help

## Screencast (Asciinema)

![Demo](script.svg)

## Global Help

Invoking the CLI without any arguments, or with `help` or `--help` shows the global help:

```bash $? cd .././.././..
$ ./enola
...
```

Each sub-command's help can be shown either with `enola help SUBCOMMAND` or `enola SUBCOMMAND --help`.

## ListKinds

[Listing the Entity Kinds of a model](../library/index.md) has the following options:

```bash $? cd .././.././..
$ ./enola list-kinds --help
...
```

## DocGen

[Documentation Generation](../docgen/index.md) has the following options:

```bash $? cd .././.././..
$ ./enola docgen --help
...
```

## Get

[Get Entity](../get/index.md) has the following options:

```bash $? cd .././.././..
$ ./enola get --help
...
```

## List

```bash $? cd .././.././..
$ ./enola list --help
...
```

## Server

[The built-in HTTP Web Server](../server/index.md) has the following options:

```bash $? cd .././.././..
$ ./enola server --help
...
```

## ExecMD

[Executable Markdown](../execmd/index.md) has the following options:

```bash $? cd .././.././..
$ ./enola execmd --help
...
```

## Rosetta

```bash $? cd .././.././..
$ ./enola rosetta --help
...
```

## Generate Completion

```bash cd .././.././..
$ ./enola generate-completion --help
...
```
