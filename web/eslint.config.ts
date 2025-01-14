import eslint from "@eslint/js"
import tseslint from "typescript-eslint"

// https://typescript-eslint.io/getting-started/
export default tseslint.config(
  {
    ignores: ["public/bundle.js", "web-out/**/*.js"],
  },
  eslint.configs.recommended,
  tseslint.configs.recommended
) // TODO https://eslint.org/docs/latest/use/configure/configuration-files#typescript-configuration-files
