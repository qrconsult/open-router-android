**English** | [Русский](README.ru.md)
![Logo](./assets/en/logo.png)

![Education Project](https://img.shields.io/badge/education_project-8A2BE2.svg?style=for-the-badge&logoColor=white)
![Android](https://img.shields.io/badge/Android_/_views-%237F52FF?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/room-%237F52FF.svg?style=for-the-badge&logoColor=white)
![Retrofit2](https://img.shields.io/badge/retrofit2-%237F52FF.svg?style=for-the-badge&logoColor=white)
![Hilt](https://img.shields.io/badge/hilt-%237F52FF.svg?style=for-the-badge&logoColor=white)
![Flow](https://img.shields.io/badge/Flow-%237F52FF.svg?style=for-the-badge&logoColor=white)

**OpenRouter::client** — Android application for communicating with neural networks via the [OpenRouter.ai](https://openrouter.ai/) service. <br>The project was developed **for educational purposes** to strengthen skills.

## Features

* Sending requests to a neural network through the OpenRouter API
* Selecting and saving favorite AI models from the OpenRouter catalog
* **Support for free models** — filter and use free-tier models (e.g., `qwen/qwen3.6-plus:free`) without any costs
* **Bilingual interface** — switch between English and Russian in settings
* Switching context mode (using chat history in the request)
* Saving message history as a chat
* Using a custom API key
* Clearing chat history
* Detailed error reporting — see the actual error message instead of generic "check your connection"

## Screenshots

![Screenshots](./assets/en/screenshots.png)
![Screenshots of messages](./assets/en/screenshots_messages.png)

## Architecture

The project is built according to the principles of **Clean Architecture** and divided into layers:

* **Presentation** (UI, Activity, Fragments, ViewModels)
* **Domain** (UseCases, Entities, Repository interfaces)
* **Data** (Room, Retrofit, Repositories, Mappers)
* **DI** (Component, Modules, Scope, Qualifiers)

The MVVM pattern is implemented (ViewModel + Flow).<br>
Dependency Injection is handled via Hilt.

## Tech Stack

* **Kotlin**, **Android SDK**, **Coroutines**, **Flow**, **ViewBinding**
* **Room** (storing chat history and favorite AI models)
* **Retrofit2** (network requests to the OpenRouter API)
* **Hilt** (dependency injection)
* **Markwon** (markdown rendering)

## Build and Run

1. Clone the repository:

```bash
  git clone https://github.com/qrconsult/open-router-android.git
  cd open-router-android
```

2. Build the project using Gradle:

```bash
  ./gradlew assembleDebug
```

   Or open in Android Studio and use **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

3. The APK will be in `app/build/outputs/apk/debug/app-debug.apk`

4. Add your OpenRouter API key:

   * Go to [OpenRouter.ai](https://openrouter.ai/) and register.
   * Create a new API key in [profile settings](https://openrouter.ai/settings/keys).
   * Specify it in the application settings:

![Screenshot API key](./assets/en/screenshot_api_key.png)
