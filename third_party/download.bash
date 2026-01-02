#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

# Moment.js v2.30.1, as seen e.g. in https://momentjs.com/downloads/moment.js
curl -L -o third_party/web/moment@2.30.1.min.js              https://momentjs.com/downloads/moment.min.js
curl -L -o third_party/web/moment-with-locales@2.30.1.min.js https://momentjs.com/downloads/moment-with-locales.min.js

curl -L -o third_party/web/vis-data@7.1.9.min.js https://unpkg.com/vis-data@7.1.9/peer/umd/vis-data.min.js
curl -L -o third_party/web/vis-timeline-graph2d@7.7.3.min.js https://unpkg.com/vis-timeline@7.7.3/peer/umd/vis-timeline-graph2d.min.js
curl -L -o third_party/web/vis-timeline-graph2d@7.7.3.min.css https://unpkg.com/vis-timeline@7.7.3/styles/vis-timeline-graph2d.min.css
