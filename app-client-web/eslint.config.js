import js from '@eslint/js';
import eslintPluginImport from 'eslint-plugin-import'
import eslintPluginReact from 'eslint-plugin-react';
import eslintPluginReactHooks from 'eslint-plugin-react-hooks';
import eslintPluginReactRefresh from 'eslint-plugin-react-refresh';
import globals from 'globals';

export default [
    {
        ignores: [
            'dist',
        ],
    },
    {
        files: ['**/*.{js,jsx}'],
        languageOptions: {
            ecmaVersion: 2020,
            globals: {
                ...globals.browser,
                process: 'readonly',
            },
            parserOptions: {
                ecmaVersion: 'latest',
                ecmaFeatures: {
                    jsx: true,
                },
                sourceType: 'module',
            },
        },
        settings: {
            react: {
                version: '18.3',
            },
            'import/resolver': {
                alias: {
                    map: [
                        ['@', './src'],
                    ],
                    extensions: ['.js', '.jsx', '.json'],
                },
            },
            'import/extensions': [
                '.js',
                '.jsx',
                '.ts',
                '.tsx'
            ]
        },
        plugins: {
            'import': eslintPluginImport,
            'react': eslintPluginReact,
            'react-hooks': eslintPluginReactHooks,
            'react-refresh': eslintPluginReactRefresh,
        },
        rules: {
            ...js.configs.recommended.rules,
            ...eslintPluginReact.configs.recommended.rules,
            ...eslintPluginReact.configs['jsx-runtime'].rules,
            ...eslintPluginReactHooks.configs.recommended.rules,
            'react/jsx-no-target-blank': 'off',
            'react-refresh/only-export-components': [
                'warn',
                { allowConstantExport: true },
            ],
            'no-console': 'warn',
            'no-debugger': 'error',
            'no-param-reassign': 'error',
            'react/react-in-jsx-scope': 'off',
            'import/no-unresolved': 'error',
            'import/named': 'error',
            'import/no-duplicates': 'warn',
            'import/order': [
                'error',
                {
                    'groups': [
                        'builtin',
                        'external',
                        'internal'
                    ],
                    'pathGroups': [
                        {
                            'pattern': 'react',
                            'group': 'external',
                            'position': 'before'
                        },
                        {
                            'pattern': '@mui/**',
                            'group': 'external',
                            'position': 'after'
                        }
                    ],
                    'pathGroupsExcludedImportTypes': [
                        'react'
                    ],
                    'newlines-between': 'always',
                    'alphabetize': {
                        'order': 'asc',
                        'caseInsensitive': true
                    }
                }
            ]
        },
    },
    {
        files: ['vite.config.js'],
        languageOptions: {
            globals: globals.node,
        },
    },
    {
        files: ['**/*.test.{js,jsx}'],
        languageOptions: {
            globals: globals.jest,
        },
    },
]
