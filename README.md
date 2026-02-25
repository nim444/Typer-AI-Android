
[![Unit Tests](https://github.com/Soluzy/Typer-AI-Android/actions/workflows/build.yml/badge.svg)](https://github.com/Soluzy/Typer-AI-Android/actions/workflows/build.yml)
____
<br>


![Banner](assets/banner.png)

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

An AI-powered Android typing assistant that fixes grammar and adjusts tone — accessible instantly from your Quick Settings tile or a persistent floating bubble.

You're in any app, you have a messy draft. Swipe down, tap the **Typer** tile, type or speak your text, pick a tone, and get a clean rewrite — then tap **Copy & Close** to paste it wherever you were. No overlay permissions needed. No switching apps. Just a floating popup over your current screen.

![Demo](assets/demo.gif)




---

### License

**PolyForm Noncommercial License 1.0.0**

This software is licensed for non-commercial use only. You may use this project for personal, academic, and non-profit purposes. **Commercial use, including but not limited to selling this software or using it as part of a paid service, is strictly prohibited.**

See the [LICENSE](LICENSE) file for the full legal text.

___

<br>
<details>
  <summary>1. Features</summary>

- **Quick Settings Tile & Floating Button** — instant access to the AI from anywhere on Android
- **Multi-Provider AI Options** — seamlessly pick between Google Gemini (2.5 & 3.0) and xAI Grok (4.1 & 3 Mini)
- **Dedicated Settings App** — beautifully themed settings UI with light/dark adaptive designs and dynamic header images
- **Customizable Pre-prompt** — write your own base instructions (e.g., "Rewrite to fix grammar and improve clarity")
- **Voice Input** — tap the mic to let Android's built-in Speech-to-Text do the typing
- **Copy & Close** — automatically copies the polished text to your clipboard and dismisses the window
- **Hide API Keys** — secure password-style visibility toggles for your saved API keys
- **Persistent Secure Storage** — Local DataStore preferences save everything safely on-device
</details>

<details>
  <summary>2. Setup & Installation</summary>

#### Get an API Key (Gemini or Grok)
1. **Gemini:** Go to [aistudio.google.com/apikey](https://aistudio.google.com/apikey) to generate a free key.
2. **Grok:** Go to [console.x.ai](https://console.x.ai/) to generate a Grok API key.

#### Install the APK
1. Go to the [Actions tab](../../actions) on GitHub.
2. Open the latest successful **Build Android APK** run.
3. Download the `Typer-APK` artifact and unzip it.
4. Transfer `app-debug.apk` to your Android device.
5. Enable **Install from unknown sources** in your device settings and install.
</details>

<details>
  <summary>3. How To Use</summary>

1. **Open the Typer app** from your app drawer.
2. **Configure your AI Providers**: Enter your Gemini and/or Grok API keys and select your preferred models from the dropdowns. Choose your Default Provider.
3. Customize your **App Theme** and toggle the **Floating Button** if you want a permanent on-screen shortcut.
4. Alternatively, add the **Typer** Quick Settings tile to your notification shade.
5. From any other app, drop down your notifications and tap the **Typer** tile or the floating bubble.
6. A translucent popup will appear. Type your draft **or** tap the **mic** to speak it.
7. Tap **Fix Grammar**.
8. Review the result, then tap **Copy & Close** to paste into any app.
</details>

<details>
  <summary>4. API Tier Limits (Reference)</summary>

| Provider | Model | Typical Free Tier |
|---|---|---|
| Google (Gemini) | `gemini-2.5-flash` | 15 RPM / 1M TPM / 1,500 RPD |
| xAI (Grok) | `grok-beta` series | Approx $5.00/1M tokens (depends on active tier) |

*Always verify up-to-date quotas with your respective provider console.*
</details>

<details>
  <summary>5. Tech Stack & Project Structure</summary>

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| AI | Google Gemini (1.5, 2.0, 2.5, 3.0 via SDK) & Grok (xAI API via pure HTTP) |
| Storage | DataStore Preferences |
| Voice | Android SpeechRecognizer |
| Entry points | Floating Bubble Service, Quick Settings Tile, Direct Launcher |
| Build | Gradle 8.7 / AGP 8.6.1 / Kotlin 1.9.22 |
| CI | GitHub Actions → debug APK artifact |

```text
Typer-AI-Android/
├── app/src/main/
│   ├── java/com/tyzytyper/
│   │   ├── SettingsActivity.kt    # Main Settings UI (AI Providers, Theme)
│   │   ├── PopupActivity.kt       # Voice Input / AI Resolution overlay
│   │   ├── FloatingButtonService.kt # Persistent floating bubble entry point
│   │   ├── TyperTileService.kt    # Quick Settings tile target
│   │   ├── AiService.kt           # Network dispatcher for Grok and Gemini
│   │   ├── SettingsManager.kt     # DataStore preferences persistence
│   │   └── ui/theme/              # Custom Blue Material 3 Theme Configuration
│   ├── res/drawable/
│   │   └── ic_tile.xml            # QS tile icon
│   └── AndroidManifest.xml
├── .github/workflows/
│   └── build.yml                  # CI: builds debug APK
├── app/build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
</details>
