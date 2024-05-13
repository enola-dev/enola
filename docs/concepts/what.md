<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024 The Enola <https://enola.dev> Authors

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

# Things & Resources

Enola's main concepts are _Things_ and _Resources._

## Things

Let's talk about things.

In Enola, everything is a _Thing!_ Things have _Properties_ which are identified by _Predicates_ and a _Datatype._

Things have an _[Identity](which.md)._ which lets us _link_ all Things. (At least most of them do; but there can also be "inner Things", which are "anonymous").

[Implementations Details](../dev/implementation.md) has more internal technical details about these things.

## Resources

_Resources_ are what Enola calls things which are just 010011s - without properties like Things. Resources are identified by an URL.

An HTML page on a web server, or a JPEG photo image in a local file on your computer are both examples of a _Resource._

Some resources can be viewed as (converted to) Things.
