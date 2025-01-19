#!/usr/bin/env -S bun --experimental-html run

// TODO Run tsc & test by using e.g. `concurrently`? Or no need?
//   Use https://github.com/google/zx/ or https://github.com/dsherret/dax
//   instead of Bun's $ (see also https://github.com/google/zx/pull/1082)
//   if they handle (contrib?) https://github.com/oven-sh/bun/issues/16496 ?

import { serve } from "bun"
import index from "../../public/index.html"

const PORT = 7070
const IGNORE = ["/favicon.ico"]

serve({
  port: PORT,
  development: true,
  static: {
    "/": index,
  },
  async fetch(req) {
    if (IGNORE.includes(req.url)) {
      return new Response("🪹 No Content ", { status: 204 })
    }
    return new Response("🙅🏽‍♀️ Not Found", { status: 404 })
  },
})

console.log(`🚀 Server running at http://localhost:${PORT}`)
