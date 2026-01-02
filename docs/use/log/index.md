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

# Logging 🐞

Enola "out of the box" is "silent", and does not "print what it's doing".

But the _verbosity_ command-line argument allows to "see what's going on internally":

* `-v` will show only _"error"_ (AKA _"severe")_ log messages
* `-vv` will additionally also show _"warning"_ log messages
* `-vvv` will additionally also show _"information"_ log messages
* `-vvvv` will additionally also show _"debug"_ (AKA _"debug"_ and _"fine")_ log messages
* `-vvvvv` will additionally also show _"more debug"_ (AKA _"finer")_ log messages
* `-vvvvvv` will additionally also show _"trace"_ (AKA _"finest")_ log messages
* `-vvvvvvv` will show all available log messages

The [Chat UI Server](../server/index.md#chat) also has a UI which shows which Agents & Tools were called.
