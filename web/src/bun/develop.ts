#!/usr/bin/env -S bun --experimental-html run

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

// TODO Also run tsc & test in BG by using e.g. `concurrently`? Or no need?

import { file, serve } from "bun"
import chat from "../../public/chat.html"
import index from "../../public/index.html"

const PORT = 7070
const IGNORE = ["/favicon.ico"]

serve({
  port: PORT,
  development: { hmr: false },

  routes: {
    "/": index,
    "/chat": chat,
  },
  fetch(req) {
    const path = new URL(req.url).pathname
    if (path.startsWith("/demo")) return new Response(file(`./public${path}`))
    else if (IGNORE.includes(path)) return new Response("🪹 No Content ", { status: 204 })
    else return new Response("🙅🏽‍♀️ Not Found", { status: 404 })
  },
})

console.log(`🚀 Server running at http://localhost:${PORT.toString()}`)
