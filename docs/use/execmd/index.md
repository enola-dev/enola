<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2025 The Enola <https://enola.dev> Authors

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

# Executable Markdown (MD) 📝

_ExecMD_ is a tool ([not a Dr.](https://memory-alpha.fandom.com/wiki/I%27m_a_doctor,_not_a...)) which executes commands from Markdown code blocks. Inspired by [Literate Programming](https://en.wikipedia.org/wiki/Literate_programming), this is useful e.g. to create documentation which automatically updates itself to stay in line with current code of tools being documented - such as [later on this page](#cli-options)! 😃

## Usage

A code block such as this one in [`demo.md`](demo.md):

```yaml
    ```bash
    $ echo Hi
    WHATEVER IS WRITTEN HERE IS IGNORED
    ```
```

when ran through `./enola execmd docs/use/execmd/demo.md` produces:

```bash
$ echo Hi
OVERWRITTEN
```

## MD Format

* Only ` ``` ` fenced code blocks are processed. (Any `~~~` or inline `` ` are ignored.)
* Only `bash` language format code blocks are executed. (Other languages are ignored.)
* The code block must start with `$` which must be followed by the command to run
* As long as lines end with `\` they are appended to the command to run
* All lines after are ignored (and replaced, with CLI option `--inline`)
* The Markdown file may contain several such code blocks

Some flags can be specified in the _preamble_ after ` ```bash`:

* ` ```bash $%` = command's exit code is completely ignored
* ` ```bash $?` = command is expected to have non-zero exit code
* ` ```bash INIT` = runs `INIT` before the command after the `$` (but "hidden" from MD)

The tool does the following:

* The line is not directly `exec`, but passed as-is to `bash -c`
* The working directory is set to the processed Markdown file's directory (use e.g. ` ```bash cd ..` to change it)
* Command must exit with `0`, otherwise the tool aborts and returns that code (unless ` ```bash $?` or `$%` is used)
* Command is killed (times out) after 7s if it "hangs" (useful on CI)
* `STDOUT` & `STDERR` are both captured, and interspersed in MD
* `STDIN` is closed (but you can use `< ...`)
* _TODO_ `TERM` ... ? 🤔

## Script Extraction

The commands in the MD are also extracted and written into a file named `script` next to the MD. This can be used e.g. to automagically record screencast videos, such as those on other pages of this documentation.

## CLI Options

```bash $? cd ../.././..
$ ./enola execmd --help
...
```

## Noteworthy

* Using `&` inside the code block, e.g. to start a server in the background for a demo, is a PITA. Instead, put such a demo with the `&` into a script, and call that script in the code block.
* Beware of the
  _"[Fork Bomb](https://en.wikipedia.org/wiki/Fork_bomb)"_ 😈 which would happen if a Markdown file were to to include an `execmd` command on itself!

## Inspiration

* <https://zimbatm.github.io/mdsh/>
* <https://github.com/zombocom/rundoc>
* <https://github.com/khalidx/runbook>
* <https://github.com/eclecticiq/rundoc>
* <https://codeberg.org/mhwombat/run-code-inline>
* <https://github.com/jgm/pandoc/wiki/Pandoc-Filters#running-code-related>
