# Contributing

Thank you for considering a contribution to Accountant Assistant.

## Before you start

- Search existing issues and pull requests to avoid duplicates.
- Open an issue before a substantial change so the approach can be discussed.
- Do not include personal financial data, credentials, signing keys, `google-services.json`, or other secrets in issues, commits, screenshots, or test fixtures.

## Local setup

1. Fork and clone the repository.
2. Create your own Firebase Android app and place its `google-services.json` in `app/`.
3. Copy `local.properties.example` to `local.properties` and use test credentials.
4. Open the project with Android Studio and sync Gradle.

Never reuse the maintainer's production Firebase, AdMob, Google Play, or signing credentials.

## Development workflow

1. Create a focused branch from `dev`.
2. Keep changes small and use clear commit messages.
3. Run `./gradlew testDebugUnitTest assembleDebug`.
4. Remove generated files and inspect the diff for secrets or personal data.
5. Open a pull request targeting `dev` and complete the template.

By contributing, you agree that your contribution may be distributed under the repository's eventual license. Until a license is added, all rights remain with their respective copyright holders.
