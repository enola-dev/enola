// TODO How to make this .ts as well?

// TODO Run tsc from here instead of package.json, see the TODO in the README.md

// TODO Enable https://esbuild.github.io/api/#live-reload

// TODO Can this have a #!/usr/bin/env node shebang and made chmod +x executable?

import { build } from "esbuild"

// TODO await ? https://esbuild.github.io/getting-started/#build-scripts
build({
  // TODO minify: false in development... how to pass in like a CLI arg type thing?
  minify: true,
  // TODO sourcemap: false in production... how to pass in like a CLI arg type thing?
  sourcemap: true,
  // Watch should be used in development...

  // NOTE: We intentionally feed the TSC output to ESBuild,
  // instead of using "script.ts" for https://esbuild.github.io/content-types/#typescript.
  // We could of course run as  "tsc -noEmit" in parallel, but this seems much cleaner to us.
  entryPoints: ["web-out/script.js"],
  outfile: "public/bundle.js",

  bundle: true,
  format: "esm",
  platform: "browser",
  target: "es2022", // Keep in sync with "target" in tsconfig.json
}).catch(() => process.exit(1))

// TODO Bundle CSS as well

// TODO https://github.com/esbuild/community-plugins ?
