<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

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

# Enola 🕵🏾‍♀️ ToDo

## High Priority (Next Features)

1. Focus on implementing _Applications_ outlined in [`mkdocs.yaml`](mkdocs.yaml)

1. Merge this with [`ToDo.yaml`](ToDo.yaml)

1. Support top-level `namespace: "demo"` in `docs/use/library/model.textproto` and rm others

### UI

1. _#css_ Units `px` -VS- `em`? Uniform?!

1. _#css_ `.app-bar` (incl. Help icon) middle alignment how-to?

1. _#css_ Search Bar should be more in the middle, and fill all space

1. _#css_ How to `vertical-align: top` the "inner" Thing table/s?

1. _#feat_ Collapse Thing value cells on click (perhaps with a [+/-] ?)

1. _#dev_ `Resource.watch()` etc. with https://github.com/livereload/livereload-js

1. _#dev_ It's ugly how 404.html and other HTML templates duplicate the HTML "frame" - how-to re-use nicely? With Mustache Templates?
   https://github.com/google/closure-templates/blob/master/documentation/reference/template-types.md#how-do-you-pass-in-a-template

---

## Low Priority / Nice To Have

1. Icon based on some Status `data` from Connector; e.g. 📗 / 📘 / 📙 / 📕

1. https://squidfunk.github.io/mkdocs-material/setup/setting-up-site-analytics/

1. LinuxMachine MVP; incl. dmsg Kernel Log analyzer, https://github.com/prometheus/node_exporter reader, etc.

1. Fix mediaType from extension so that e.g. this will work:

       ./enola -v --load=https://raw.githubusercontent.com/enola-dev/enola/main/core/impl/src/test/resources/demo-model.textproto docgen

1. Add support for writing and reading `ProtobufMediaTypes.PROTOBUF_YAML_UTF_8` - easy, given that _YAML is JSON?_

1. https://github.com/enola-dev/enola/issues/102 :
   Enforce https://www.conventionalcommits.org like git commit messages
   starting with feat/model/fix/build/docs/clean/format/refactor: and core/k8s/tools:
   using https://github.com/jorisroovers/gitlint

1. CLI completion - hard or trivial?

1. Move `common/common/src/[main|test]**/protobuf` to `common/protobuf` (requires ServiceLoader in `MediaTypeDetector`)

1. https://github.com/google/flogger/tree/master/grpc/src/main/java/com/google/common/flogger/grpc ?

1. Upstream `CommandLineSubject` to Picocli?

1. unifiedjs.com ?

1. Add to https://github.com/SquadcastHub/awesome-sre-tools

1. https://plugins.jetbrains.com/plugin/10456-prettier ? Or no longer needed, with VSC?
1. [Bazel Java IDE support](https://github.com/vorburger/LearningBazel/blob/develop/ToDo.md) in VSC instead IJ?!

1. docs: Make `tools/docs/serve.bash` use [`ibazel`](https://github.com/bazelbuild/bazel-watcher) to rebuild Proto MD on change of `.proto`

1. https://github.com/textlint/textlint with https://github.com/textlint/textlint/wiki/Collection-of-textlint-rule

1. `dev.enola.common.io.mediatype` adapter for https://tika.apache.org/2.7.0/api/org/apache/tika/detect/Detector.html

1. [Run `mkdocs build` instead of in `build.bash` as a `sh_test` in Bazel with `docs/**` + `mkdocs.yaml` as (only) `srcs`](https://github.com/enola-dev/enola/compare/main...vorburger:enola:mkdocs_build_test) - fix weird problems

1. Proto design: Is this a real requirement, or can we forget about this: _"Note that IDs are not "unique", and 2
   different IDs may refer to the same underlying object; for example: `k8s:pod?name=echoserver-6dfb6c7764-45gvk&...`
   and `k8s:pod?uid=561f1bec-f768-4c5b-b96e-37306d7f2f8a&...`"_

1. Proto design: Should `EntityKind` have a _"param_parent, to allow grouping common ones, just during declaration in
   textproto, but inlined for use."_ or shall we forget about that?

1. Proto design: Should we permit RPC clients to _"specify the ID in either (oneof)string text or "broken down" parts
   form. This is simply for dev convenience in UX such as CLI or Web UIs, and to avoid the proliferation of incompatible
   parsers. The implementation validates the text, and rejects e.g. "demo:foo?bad=a=b" or "demo:foo?bad=a&bad=b". The
   string text oneof form is NOT "decoded" like un-escaped (incl. un-quoted) at all, simply "split" ... ? There
   was `IDsTest.java` and `IDs.java` code related to this which I removed on 2023-03-19:_

       message ID {
           oneof oneof {
               string text = 1;
               Parts parts = 2;
           }
           message Parts {
               string scheme = 1;
               string entity_kind = 2;
               repeated string segments = 3;
           }
       }
