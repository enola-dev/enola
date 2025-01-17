import { build } from "bun"

// TODO Can this have a #!/usr/bin/env bun shebang and be made chmod +x executable?

// TODO Run tsc from here instead of package.json, e.g. by using `concurrently` or `npm-run-all` (README.md)

// TODO Support watch mode, ideally like https://esbuild.github.io/api/#live-reload

const result = await build({
  entrypoints: ["src/browser/index.ts"],
  outdir: "public/bundles/",

  // TODO minify: false in development... how to pass in like a CLI arg type thing?
  // TODO sourcemap: false in production... how to pass in like a CLI arg type thing?
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
