# Pixel-Tech-Test

## Overview

Pixel-Tech-Test is an Android application written in Kotlin using Jetpack Compose. It fetches and displays the top 20 StackOverflow users, showing their profile image, name, and reputation. Users can be "followed" locally, with the follow status persisting between sessions.

## Features

- Fetches top 20 StackOverflow users from the public API.
- Displays user profile image, display name, and reputation.
- Allows users to "follow" or "unfollow" StackOverflow users.
- Follow status is stored locally and persists between app launches.
- Shows an error message and empty state if the API is unavailable.
- Built with Jetpack Compose for UI.
- No third-party frameworks used.
- Unit tests included for core logic.

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/Pixel-Tech-Test.git
   ```
2. Open the project in Android Studio (2024.3.2 or newer recommended).
3. Build and run the app on an emulator or device with Android 8.0+.

## Usage

- Launch the app to see the list of StackOverflow users.
- Tap "Follow" to follow a user; tap "Unfollow" to remove.
- Followed users are indicated in the list.
- If the app cannot reach the API, an error message is shown.

## Technical Decisions & Explanations

### Architecture

- **MVVM Pattern:** Chosen for separation of concerns, testability, and clarity. ViewModels handle business logic and state, Compose manages UI.
- **Repository Layer:** Abstracts data fetching and local persistence, making it easier to test and maintain.
- **Local Persistence:** Used `SharedPreferences` for simplicity and to meet the "no third-party frameworks" requirement. This keeps follow status between sessions.

### UI

- **Jetpack Compose:** Required by the spec; provides a modern, declarative way to build UI.
- **Error Handling:** The UI displays an empty state and error message if the API fails, improving user experience.

### Testing

- **Unit Tests:** Core logic (e.g., follow/unfollow, data parsing) is covered by unit tests to ensure reliability and facilitate future changes.
- **No Third-Party Libraries:** All code is written using standard Android and Kotlin libraries to comply with requirements.

### API

- **StackOverflow API:** Uses the provided endpoint to fetch user data. Handles network errors gracefully.

## Limitations

- No actual "follow" API calls; follow status is simulated and stored locally.
- No pagination or search; only the top 20 users are shown.

## How to Run Tests

- Run unit tests from Android Studio using the built-in test runner.

## Why These Choices?

- **MVVM** improves maintainability and testability.
- **Compose** is modern and required.
- **No third-party frameworks** keeps the code simple and demonstrates core Android/Kotlin skills.
- **Local persistence** with SharedPreferences is lightweight and sufficient for the requirements.
