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

# Chat

<!-- TODO "This is the last shell you'll ever need." -->

<!-- TODO This is increasingly less readable...
       ... use an (EOF) "HERE doc", even though that's Bash and NOK in Fish?
       ... use a file instead, shown here? -->

```bash cd ../.././..
$ echo -e "hello, world\n@echo hi\n/help\n/whoami" | ./enola chat
...
```

[See Help doc](../help/index.md#chat) for all options.

## AI

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
