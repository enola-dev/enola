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

# Chat 💬 (Shell)

Enola’s Chat is _The Shell for the 2030s+_ and the _“last Chat you'll ever need”._

This is the text-based Console UI ("TUI"); for the Web UI, see [`server`](../server/index.md#chat).

[After installing](../index.md), you can use it like this:

## Screencast

![Demo](script.svg)

## Usage

```bash cd ../.././..
$ HOSTNAME=demo ./enola chat <docs/use/chat/demo.input
...
```

## Exec

Note how the last two "messages" in the example chat above were [`whoami`](https://en.wikipedia.org/wiki/Whoami) and [`pwd`](https://en.wikipedia.org/wiki/Pwd).

They were both executed as commands on the local system. (Whereas the `/whoami` with slash was not a system executable but a built-in command.)

In order to avoid confusion with certain terms which are both typically valid UNIX command names but also valid natural language words which you may well start a prompt to the LLM, a hard-coded list excludes them from being executed as commands (e.g. `who` and `time` or `uptime` etc.).

Prefixing input with $ followed by a space (to avoid potential future confusion with environment variables) will force it to be interpreted as a command to be executed instead (e.g. `$ uptime`).

Commands are currently executed using `/usr/bin/env bash -c ...`, but this may be changed in the future.

## AI

???+ info

    This will soon be more tightly & fully integrated with the [other Agentic AI chat features](../../tutorial/chat.md) of Enola; watch this space!

If you have [Ollama](https://ollama.com/) up and running locally on its default port `11434`, then this _Chat_ will have an `LLM>` participant using [`gemma3:1b`](https://ai.google.dev/gemma) which will chime into the conversation, like this:

```sh
$ ./enola chat
Welcome here! Type /help if you're lost.

vorburger@yara in #Lobby> hi
LLM> Hi there! How’s your day going so far? 😊
Is there anything you'd like to chat about, or anything I can help you with today?

vorburger@yara in #Lobby> how ya feel'
LLM> As an AI, I don't have feelings in the same way humans do. However, I can say that I’m functioning well and ready to assist you! 😊

It’s a good day for me to be here. How about you? How are *you* feeling today?
```

This will be extended to support other 🔮 LLMs and 🪛 Tools and 🕵🏾‍♀️ Agents in the future - watch this space.

## SSH

This _Chat_ feature is [also available via an SSH server](../server/index.md#ssh)!

[Exec](#exec) is only enabled in (local) `enola chat`, but disabled over SSH.

`/whoami` includes the public key of the user connected over SSH.

## Configuration

Your [`~/.inputrc` and `/etc/inputrc`](https://www.linuxfromscratch.org/lfs/view/12.3/chapter09/inputrc.html) are loaded on startup. [For example](https://github.com/vorburger/vorburger-dotfiles-bin-etc/blob/032a76d83ec26a79b84dc44e0e7b8a52132812ab/dotfiles/.inputrc#L36), to bind `Ctrl-Backspace`, do `echo '"\C-h": backward-kill-word' >> ~/.inputrc`.

The line editor has _mouse support,_ e.g. to click on the prompt line to move the cursor.

History is persisted e.g. in `~/.local/share/enola/history`.

[See Help doc](../help/index.md#chat) for all other options.
