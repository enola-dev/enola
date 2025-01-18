#!/usr/bin/env bun

import { $, build } from "bun"

// TODO Adopt https://bun.sh/docs/bundler/fullstack

// TODO Run tsc & test by using `concurrently` or `npm-run-all`

// TODO Use https://github.com/google/zx/ or https://github.com/dsherret/dax
//   instead of Bun's $ (see also https://github.com/google/zx/pull/1082)
//   if they handle (contrib?) https://github.com/oven-sh/bun/issues/16496 ?

// TODO FIXME await $`bun tsc`

await $`bun test`

const result = await build({
  html: true,
  experimentalCss: true,

  entrypoints: ["public/index.html"],
  outdir: "web-out/",

  minify: true,
  sourcemap: "linked",

  naming: "./[dir]/[name]-[hash].[ext]",

  throw: true,
})

if (result.success) {
  // result.outputs.map(output => console.log("✅", output.path))
  console.log("✅ Successfully 📦 bundled!")
  process.exit(0)
} else {
  console.error("Build failed:", result.logs)
  process.exit(1)
}
