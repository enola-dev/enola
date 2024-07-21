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

set -euox pipefail

# WITH https://github.com/digitalbazaar/jsonld-cli

npx jsonld-cli@2.0.0 --help

npx jsonld-cli@2.0.0 lint test/*.jsonld

# TODO https://github.com/digitalbazaar/jsonld-cli/pull/28: --context=test/picasso-context.jsonld
# NB https://github.com/digitalbazaar/jsonld-cli/issues/15
cd test
npx jsonld-cli@2.0.0 flatten picasso.json | tee /tmp/picasso.jsonld
cd ..

# TODO https://github.com/digitalbazaar/jsonld-cli/pull/27: npx jsonld-cli@2.0.0 diff test/picasso.jsonld /tmp/picasso.jsonld
./enola canonicalize --pretty --load=file:test/picasso.jsonld --output=file:/tmp/picasso-expected.canonicalized.jsonld
./enola canonicalize --pretty --load=file:/tmp/picasso.jsonld --output=file:/tmp/picasso.canonicalized.jsonld
diff /tmp/picasso.canonicalized.jsonld /tmp/picasso.canonicalized.jsonld

# WITH https://github.com/filip26/ld-cli

# TODO


# WITH ./enola itself!

# TODO
