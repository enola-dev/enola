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

./enola canonicalize --pretty --load=file:test/picasso.jsonld --output=file:/tmp/picasso-expected.canonicalized.jsonld


# WITH https://github.com/digitalbazaar/jsonld-cli
npx jsonld-cli@2.0.0 --help
npx jsonld-cli@2.0.0 lint test/*.jsonld
# TODO https://github.com/digitalbazaar/jsonld-cli/pull/28: --context=test/picasso-context.jsonld
# NB https://github.com/digitalbazaar/jsonld-cli/issues/15
npx jsonld-cli@2.0.0 flatten --base=test/ test/picasso.json | tee /tmp/picasso-digitalbazaar.jsonld
# TODO https://github.com/digitalbazaar/jsonld-cli/pull/27: npx jsonld-cli@2.0.0 diff test/picasso.jsonld /tmp/picasso.jsonld
./enola canonicalize --pretty --load=file:/tmp/picasso-digitalbazaar.jsonld --output=file:/tmp/picasso-digitalbazaar.canonicalized.jsonld
diff /tmp/picasso-digitalbazaar.canonicalized.jsonld /tmp/picasso-expected.canonicalized.jsonld


# WITH https://github.com/filip26/ld-cli
if [ ! -f .cache/ld-cli ]; then
  curl -L -o .cache/ld-cli.zip https://github.com/filip26/ld-cli/releases/download/v0.8.0/ld-cli-0.8.0-ubuntu-latest.zip
  unzip .cache/ld-cli.zip -d .cache/
  rm .cache/ld-cli.zip
  chmod +x .cache/ld-cli
fi
.cache/ld-cli flatten --mode=1.1 --input=file:"$PWD"/test/picasso.json >/tmp/picasso-filip26.jsonld
./enola canonicalize --pretty --load=file:/tmp/picasso-filip26.jsonld --output=file:/tmp/picasso-filip26.canonicalized.jsonld
diff /tmp/picasso-filip26.canonicalized.jsonld /tmp/picasso-expected.canonicalized.jsonld

# WITH ./enola itself!

# TODO
