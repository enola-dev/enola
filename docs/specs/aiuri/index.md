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

# AI URI

_Original Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Contributors:_ **YOU, contribute?**
<br>_Status:_ **Ready to Implement**

## Introduction

[As you know](https://martinfowler.com/bliki/TwoHardThings.html), _"There are only two hard things in Computer Science: cache invalidation and naming things."_

This spec proposes a convention for consistently naming some _things_ related to AI with [URIs](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier).

These URIs are independent of any specific programming language, framework, tool, etc.

Perhaps your favorite one could adopt it!

## Language Models (LM)

### Ollama 🦙

`http://localhost:11434?type=ollama&model=gemma3:1b` is the URI of the [Gemma 3 (1B)](https://ai.google.dev/gemma/) large language model running in [Ollama](https://ollama.com), at `http://localhost:11434`.

The `model` query parameter of the URI needs to use a model name from https://ollama.com/search.

### Google AI 🔮

`google://?model=gemini-2.5-flash-preview-04-17` is the URI of the [Gemini 2.5 Flash (Preview 04-17)](https://ai.google.dev/gemini-api/docs/models) LLM, used via the [Google (Gemini) AI API](https://ai.google.dev/gemini-api/).

[For security](#security), the API key is not part of this URI, but configured elsewhere; typically in a _[secret](../../use/secret/index.md)_ named `GOOGLE_AI_API_KEY`. Such keys can be easily obtained by clicking on _Get API key_ in https://aistudio.google.com.

Note that the _Google (Gemini) AI API_ is a **different** API from the _Google Cloud Vertex AI Platform API._

### Mock 🦜

`mocklm:hello,%20world` is the URI of a [mock](https://en.wikipedia.org/wiki/Mock_object) LM which, like a 🦜 parrot, always replies with _"hello, world"_ to any prompt.

## Security

It is intentional that none of these URIs contain API keys. They are configured "elsewhere", with a mechanism specific to the [supporting framework or tool](#support); typically in a _[secret](../../use/secret/index.md)._

This is primarily for security reasons, as such URIs may be used e.g. in agent configuration files stored under source control, or even shown to technical end-users in UIs.

It is also often more convenient in practice; because while the choice of a model, and its parameters, is often specific to a given Agent configuration, the specific API key really is not, and instead belongs to separate configuration.

## Related

We are not aware of any similar LM URI naming scheme. Please add any that you know of here.

## ToDo

1. Add many more URIs
1. Support (default) "standard" `?topP` / `topK`, `temperature`, `maxOutputTokens`, `seed`, `stopSequences`, `version` (?) etc. query parameters (see also [dotprompt config](https://google.github.io/dotprompt/reference/frontmatter/#config))

## Support

* [Enola](../../use/chat/index.md)
* ?

## [Haiku](https://github.com/enola-dev/enola/pull/1390)

```english
A URI's a name,
For AI, it's the same,
Ollama, Google, ...
No longer a struggle,
To call models by name.
```
