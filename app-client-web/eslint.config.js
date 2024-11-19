import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'

export default tseslint.config(
  { ignores: ['dist'] },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
  },
)

//{
//    "root": true,
//    "env": {
//        "browser": true,
//        "es2020": true
//    },
//    "plugins": [
//        "import",
//        "react",
//        "react-refresh",
//        "@typescript-eslint"
//    ],
//    "extends": [
//        "eslint:recommended",
//        "plugin:react/recommended",
//        "plugin:react-hooks/recommended",
//        "plugin:@typescript-eslint/recommended"
//    ],
//    "parser": "@typescript-eslint/parser",
//    "parserOptions": {
//        "sourceType": "module",
//        "ecmaVersion": "latest",
//        "ecmaFeatures": {
//            "jsx": true
//        }
//    },
//    "rules": {
//        "react-refresh/only-export-components": "warn",
//        "no-console": "warn",
//        "no-debugger": "error",
//        "no-param-reassign": "error",
//        "react/react-in-jsx-scope": "off",
//        "import/no-unresolved": "error",
//        "import/named": "error",
//        "import/no-duplicates": "warn",
//        "import/order": [
//            "error",
//            {
//                "groups": [
//                    "builtin",
//                    "external",
//                    "internal"
//                ],
//                "pathGroups": [
//                    {
//                        "pattern": "react",
//                        "group": "external",
//                        "position": "before"
//                    },
//                    {
//                        "pattern": "@mui/**",
//                        "group": "external",
//                        "position": "after"
//                    }
//                ],
//                "pathGroupsExcludedImportTypes": [
//                    "react"
//                ],
//                "newlines-between": "always",
//                "alphabetize": {
//                    "order": "asc",
//                    "caseInsensitive": true
//                }
//            }
//        ]
//    },
//    "ignorePatterns": [
//        "src/generated/*.js",
//        "src/gql/*.js",
//        "vite.config.ts"
//    ],
//    "settings": {
//        "react": {
//            "version": "detect"
//        },
//        "import/resolver": {
//            "typescript": {
//                "project": "tsconfig.json"
//            }
//        },
//        "import/extensions": [
//            ".js",
//            ".jsx",
//            ".ts",
//            ".tsx"
//        ]
//    },
//    "overrides": [
//        {
//            "files": [
//                "**/*.test.{js,jsx,ts,tsx}"
//            ],
//            "env": {
//                "jest": true
//            }
//        }
//    ]
//}
//
