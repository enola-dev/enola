import eslint from "@eslint/js"
import tseslint from "typescript-eslint"

export default tseslint.config(
  {
    ignores: ["public/bundle.js", "web-out/**/*.js"],
  },

  // https://typescript-eslint.io/getting-started/
  // https://typescript-eslint.io/users/configs
  // https://typescript-eslint.io/getting-started/typed-linting/
  eslint.configs.recommended,
  tseslint.configs.strictTypeChecked,
  tseslint.configs.stylisticTypeChecked,

  {
    languageOptions: {
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      },
    },
  }
) // TODO https://eslint.org/docs/latest/use/configure/configuration-files#typescript-configuration-files
