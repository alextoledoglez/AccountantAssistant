<div align="center">

# Accountant Assistant

**Personal finance manager for Android — cards, purchases, and bills in one place.**

[![Release Build](https://github.com/alextoledoglez/AccountantAssistant/actions/workflows/release.yml/badge.svg)](https://github.com/alextoledoglez/AccountantAssistant/actions/workflows/release.yml)
[![Version](https://img.shields.io/badge/version-1.1.4-blue)](https://github.com/alextoledoglez/AccountantAssistant/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.05-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

</div>

---

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Home.png" alt="Home"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Wallet.png" alt="Wallet"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Buys.png" alt="Buys"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Bills.png" alt="Bills"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Menu.png" alt="Menu"/>
</p>

---

## Features

### Home — Financial Summary

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Home.png" alt="Home"/>
</p>

A quick snapshot of your financial health based on your income, expenses by period, and total
spending.
Instantly see whether your budget covers your purchases and bills for the selected period.

---

### Wallet — Cards & Income

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Wallet.png" alt="Wallet"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Wallet details.png" alt="Wallet Details"/>
</p>

Define your credit/debit cards or general income sources.
The amounts saved here feed directly into the Home summary to give you an accurate financial
overview.

---

### Buys — Smart Shopping List

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Buys.png" alt="Buys"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Buys details.png" alt="Buys Details"/>
</p>

Stop writing shopping lists on paper or editing notes by hand.
Add items once, toggle them as you pick them up at the store, and the app computes the running total
automatically.
Adjust quantities on the fly to stay within budget.

- Use the **scan** button to read a product barcode and autofill its name.
- Use the **+** button to add items manually.

---

### Bills — Recurring Payments

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Bills.png" alt="Bills"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Bills details.png" alt="Bills Details"/>
</p>

Track recurring bills (water, electricity, internet, etc.) with expiration dates.
The app sends a notification before a bill is due so you never miss a payment.
Toggle bills on/off to mark them as paid or defer them to a future date.

- Use the **scan** button to read a QR code from a bill slip and autofill company and payment
  details.
- Use the **+** button to add bills manually.

---

### Scanner — Barcode & QR Reader

<p align="center">
  <img width="240" src="app/screenshots/Accountant Assistant - Scanner QR.png" alt="QR Scanner"/>
  <img width="240" src="app/screenshots/Accountant Assistant - Scanner Barcode.png" alt="Barcode Scanner"/>
</p>

Powered by **ML Kit**, the scanner handles QR codes and barcodes through a single reactive
flow built with Compose and StateFlow.
Both code types are supported in the following contexts:

| Context | What gets filled               |
|---------|--------------------------------|
| Buys    | Product name                   |
| Bills   | Company name + payment details |

---

## Tech Stack

| Layer           | Technology                                          |
|-----------------|-----------------------------------------------------|
| Language        | Kotlin 2.1 + Coroutines / Flow                      |
| UI              | Jetpack Compose · Material 3 · BOM 2025.05          |
| Architecture    | MVVM + StateFlow + LiveData                         |
| DI              | Koin 4.2                                            |
| Database        | Room                                                |
| Camera          | CameraX                                             |
| Scanning        | ML Kit — Barcode + Text Recognition                 |
| Background work | WorkManager                                         |
| Firebase        | Crashlytics · Analytics · Performance · Realtime DB |
| Ads             | Google AdMob                                        |
| Charts          | MPAndroidChart                                      |

---

## Getting Started

### Prerequisites

- Android Studio Panda 2 | 2025.3.2 or later
- JDK 17
- Android device or emulator running API 26+

### Setup

1. Clone the repository.
2. Obtain `google-services.json` from the [Firebase Console](https://console.firebase.google.com)
   and place it under `app/`.
3. Create a `local.properties` file at the project root with the required credentials (
   see [Configuration](#configuration) below).
4. Sync Gradle and run.

### Configuration

The app requires a `local.properties` file at the project root.
This file is excluded from version control and must be created locally.
Three groups of values are needed:

| Group               | What to provide                                                                                       |
|---------------------|-------------------------------------------------------------------------------------------------------|
| **AdMob**           | App ID, ad unit ID, and test device ID — separate values for debug and release builds                 |
| **Release signing** | Keystore file (placed at the project root) along with its store password, key alias, and key password |
| **Firebase**        | Covered by `google-services.json` — no extra entries needed in `local.properties`                     |

> The exact key names expected by the build are defined in `app/build.gradle.kts`.
> Debug builds only require the AdMob credentials; signing credentials are only used for release builds.

---

## CI/CD

Three workflows separate public contribution checks from trusted release operations:

| Workflow      | Trigger                         | What it does                                                                                                      |
|---------------|---------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `debug.yml`   | PR → `dev`/`master`, push → `dev` | Uses synthetic Firebase configuration and official AdMob test IDs to run unit tests and assemble a debug APK |
| `release.yml` | Push → `master`, manual dispatch | Uses repository secrets to run tests, assemble a signed release APK, and upload it as a short-lived artifact    |
| `publish.yml` | Manual dispatch                 | Builds a signed AAB, uploads a draft to Google Play's internal track, and creates a GitHub release                 |

Pull-request workflows do not receive or require production secrets. Contributors should use their own
Firebase project and Google's test ad configuration for local development. Signing and publishing
credentials remain restricted to trusted repository workflows.

---

## Contributing

Contributions are welcome.
Please, open an issue first to discuss what you would like to change.
Then, submit a pull request targeting the `dev` branch.

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup, validation, and privacy guidance.

---

## License

Copyright 2026 Alejandro Toledo González.

Licensed under the [Apache License 2.0](LICENSE).