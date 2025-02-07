#!/usr/bin/env bun

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
import { $, build } from "bun"
import { renameSync } from "fs"
import path from "path"

// TODO Use https://github.com/google/zx/ or https://github.com/dsherret/dax
// instead of Bun's $ (see also https://github.com/google/zx/pull/1082)
// if they handle (contrib?) https://github.com/oven-sh/bun/issues/16496 ?
await $`bun tsc`

process.stdout.write("🧪 ")
await $`bun test`

console.log()
await $`rm -rf web-out/bundle/`
const result = await build({
  html: true,
  experimentalCss: true,

  entrypoints: ["public/index.html"],
  outdir: "web-out/bundle/",

  minify: true,
  sourcemap: "linked",

  naming: "./[dir]/[name]-[hash].[ext]",

  throw: true,
})

if (result.success) {
  result.outputs
    .filter(output => output.path.endsWith(".html"))
    .map(async output => {
      const dir = path.parse(output.path).dir
      const index = path.join(dir, "index.html")
      renameSync(output.path, index)
    })
  console.log("✅ Successfully 📦 bundled!")
  process.exit(0)
} else {
  console.error("Build failed:", result.logs)
  process.exit(1)
}
