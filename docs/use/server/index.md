<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2024 The Enola <https://enola.dev> Authors

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

# Web & gRPC Server

## UI

<!-- This intentionally does not use ```bash because the server "hangs" ...
     ... we COULD use --immediateExitOnlyForTest=true (as in EnolaTest),
     but this would be confusing for readers. TODO: Add support to hide
     CLI flags to Executable Markdown... ;-) -->

    $ ./enola server --model file:docs/use/library/model.yaml --httpPort=8080
    HTTP JSON REST API + HTML UI server started; open http://0:0:0:0:0:0:0:0:8080/ui ...

You can now open e.g. <http://localhost:8080/ui/entity/demo.book/ABC/0-13-140731-7/1>
to view this `demo.book` _Entity._ When you click on the _Related_ `kind` you will
see its `demo.book_kind`, where you can click e.g. on its `google` _Link._

## REST

There is also a REST API which returns JSON if you replace `ui` with `api` in the URL,
so e.g. on <http://localhost:8080/api/entity/demo.book/ABC/0-13-140731-7/1>.

## gRPC

The `--grpcPort` flag starts [the Enola gRPC API](.././../dev/proto/core#enolaservice).

This can be used by the Enola CLI Client's `--server` flag, instead of passing a `--model` file, like so:

    $ ./enola server --model file:docs/use/library/model.yaml --grpcPort=7070
    gRPC API server now available on port 9090

    $ ./enola get --server localhost:7070 demo.book/ABC/0-13-140731-7/1

which will output:

```yaml
id:
  ns: demo
  entity: book
  paths: [ABC, 0-13-140731-7, '1']
ts: 2023-12-02T20:51:44.134467Z
related:
  library:
    ns: demo
    entity: library
    paths: [ABC]
  kind:
    ns: demo
    entity: book_kind
    paths: [0-13-140731-7]
```

This is the same as a direct "in-process" [Get Entity](../get/) would have:

    ./enola get --model file:docs/use/library/model.yaml demo.book/ABC/0-13-140731-7/1

<!-- TODO Add an E2E Integration Test for what's described above,
     by Rebasing and fix https://github.com/enola-dev/enola/pull/301, so that it's testable. -->

<!-- TODO Test and illustrate that this will also work with
     https://docs.enola.dev/dev/proto/core/#data type_url for Any...
     which it normally should, but it still needs a good scenario, and docs coverage. -->

<!-- TODO Make this Executable Markup... maybe using https://github.com/google/zx for process control? -->

<!-- TODO Make this gRPC Server callable using a "generic" gRPC Client,
     such as [ktr0731's Evans](https://github.com/ktr0731/evans)
     or [asarkar's OkGRPC](https://github.com/asarkar/okgrpc).
     As-is, it won't work yet, because enola_core.proto GetFileDescriptorSet
     is not https://github.com/grpc/grpc/blob/master/src/proto/grpc/reflection/v1alpha/reflection.proto
     for https://grpc.github.io/grpc/core/md_doc_server_reflection_tutorial.html. -->
