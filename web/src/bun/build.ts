#!/usr/bin/env bun

import { build } from "bun"

// TODO Run both tsc & test from here, e.g. by using `concurrently` or `npm-run-all` (README.md)

// TODO Support watch mode, ideally like https://esbuild.github.io/api/#live-reload

const result = await build({
  entrypoints: ["src/browser/index.ts"],
  outdir: "public/bundles/",

  minify: true,
  sourcemap: "linked",

  // TODO naming: "./[dir]/[name]-[hash].[ext]" BUT then how to reference it in the HTML?!

  throw: true,
})

if (!result.success) {
  console.error("Build failed:", result.logs)
  process.exit(1)
}

// TODO How to bundle CSS as well?
