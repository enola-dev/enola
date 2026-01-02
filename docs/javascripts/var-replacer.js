/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Replaces {{variable}} in HTML body with URL's ?variable=value query parameter values.
function varReplace() {
  const params = Object.fromEntries(new URLSearchParams(location.search))
  if (!(Object.keys(params).length === 0)) {
    const template = document.getElementsByTagName("body")[0].innerHTML
    const rendered = Mustache.render(template, params)
    document.getElementsByTagName("body")[0].innerHTML = rendered
  }
}

if (window.document$) {
  document$.subscribe(varReplace)
} else {
  window.addEventListener("load", varReplace)
}
