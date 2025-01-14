import eslint from "@eslint/js"
import tseslint from "typescript-eslint"

export default tseslint.config(
  {
    ignores: ["public/bundle.js", "web-out/**/*.js"],
  },
  eslint.configs.recommended,

  // https://typescript-eslint.io/getting-started/
  tseslint.configs.strict,
  tseslint.configs.stylistic
) // TODO https://eslint.org/docs/latest/use/configure/configuration-files#typescript-configuration-files
