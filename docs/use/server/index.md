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

# Web Server

<!-- This intentionally does not use ```bash because the server "hangs" -->

    $ ./enola server --model file:docs/use/library/model.textproto --httpPort=8080
    Open http://localhost:8080/ui ...

You can now open e.g. <http://localhost:8080/ui/entity/demo.book/ABC/0-13-140731-7/1>
to view this `demo.book` _Entity._ When you click on the _Related_ `kind` you will
see its `demo.book_kind`, where you can click e.g. on its `google` _Link._
