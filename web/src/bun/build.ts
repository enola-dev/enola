#!/usr/bin/env bun

import { $, build } from "bun"

// TODO FIXME await $`bun tsc`

process.stdout.write("🧪 ")
await $`bun test`

console.log()
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
