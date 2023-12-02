#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# TODO Transform this into a Bazel target instead?
# Or an enola exec: resource?
# (Or no need, for either?)
set -euox pipefail

# TODO Install protoc,
# from https://github.com/protocolbuffers/protobuf/releases
# (NB: include/ *must* be kept; next to bin/protoc!)

# https://github.com/chrusty/protoc-gen-jsonschema
# TODO Install protoc-gen-jsonschema, via:
# go install github.com/chrusty/protoc-gen-jsonschema/cmd/protoc-gen-jsonschema@latest

protoc \
  --plugin="${HOME}"/go/bin/protoc-gen-jsonschema \
  --jsonschema_opt=allow_null_values \
  --jsonschema_opt=file_extension=schema.json \
  --jsonschema_opt=disallow_additional_properties \
  --jsonschema_out=docs/models/enola/schemas/ \
  core/lib/src/main/java/dev/enola/core/meta/enola_meta.proto

npx --yes prettier --write docs/models/enola/schemas/*.json
