# Pixel-Tech-Test

## Overview

Pixel-Tech-Test is an Android application written in Kotlin using Jetpack Compose. It fetches and displays the top 20 StackOverflow users, showing their profile information, statistics, and allowing users to follow/unfollow them locally. The app features a modern tabbed interface with navigation between different screens and comprehensive user details.

## Features

### Core Functionality
- Fetches top 20 StackOverflow users from the public API
- Displays user profile image, display name, reputation, and ranking badges
- Allows users to "follow" or "unfollow" StackOverflow users
- Follow status is stored locally using DataStore and persists between app launches
- Shows error messages and empty states when API is unavailable

### Navigation & UI
- **Tabbed Navigation**: Main interface with "All Users" and "Following" tabs
- **Following Tab**: Dedicated screen showing only followed users with counter badge
- **User Details Screen**: Comprehensive user profile with statistics, badges, and additional information
- **Search Functionality**: Search users by display name, location, or reputation on the All Users tab
- **Modern UI**: Built entirely with Jetpack Compose and Material 3 design

### User Experience
- **Dynamic Search**: Real-time filtering with search bar in the top app bar
- **User Rankings**: Visual badges showing user ranking (gold, silver, bronze) based on reputation
- **Loading States**: Smooth loading indicators and progress feedback
- **Error Handling**: Graceful error states with retry functionality
- **Responsive Design**: Optimized layouts for different screen sizes

### Technical Features
- **Hilt Dependency Injection**: Proper dependency management
- **DataStore Persistence**: Modern replacement for SharedPreferences
- **MVVM Architecture**: Clean separation of concerns with ViewModels
- **Coroutines**: Asynchronous operations with proper error handling
- **Unit Tests**: Comprehensive test coverage for core functionality

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/Pixel-Tech-Test.git
   ```
2. Open the project in Android Studio (2024.3.2 or newer recommended).
3. Build and run the app on an emulator or device with Android 8.0+.

## Usage

### Main Navigation
- Launch the app to see the tabbed interface
- **All Users Tab**: Browse all StackOverflow users with search functionality
- **Following Tab**: View only the users you're following (counter shows total)

### User Interactions
- **Follow/Unfollow**: Tap the follow button on any user card
- **Search**: Use the search icon in the top bar to filter users by name, location, or reputation
- **View Details**: Tap on any user to see their detailed profile with statistics
- **Navigation**: Use the back button or bottom tabs to navigate between screens

### User Details Screen
- Comprehensive user profile with avatar and background
- Detailed statistics including reputation, badges, and ranking
- User information such as location, website, and account details
- Follow/unfollow functionality with immediate UI feedback

## Technical Decisions & Explanations

### Architecture
- **MVVM Pattern**: Chosen for separation of concerns, testability, and maintainability
- **Repository Pattern**: Abstracts data fetching and local persistence
- **Hilt Integration**: Provides dependency injection for ViewModels and repositories
- **Navigation Component**: Handles complex navigation flows between screens

### Data Persistence
- **DataStore**: Modern replacement for SharedPreferences, providing type-safe and asynchronous data storage
- **Flow-based State Management**: Reactive programming with StateFlow for UI state updates

### UI/UX Design
- **Material 3**: Latest Material Design components and theming
- **Jetpack Compose**: Modern declarative UI toolkit
- **Responsive Layouts**: Adapts to different screen sizes and orientations
- **Visual Hierarchy**: Clear information hierarchy with proper typography and spacing

### Performance & Reliability
- **Coroutines**: Efficient asynchronous programming for network calls and data operations
- **Error Handling**: Comprehensive error states with user-friendly messages
- **Loading States**: Proper loading indicators and progress feedback
- **Memory Management**: Efficient image loading and state management

### Testing Strategy
- **Unit Tests**: Core business logic and data operations
- **UI State Testing**: ViewModel state management and user interactions
- **Repository Testing**: Data fetching and persistence operations

## API Integration

- **StackOverflow API**: Uses the official StackOverflow API v2.2
- **Endpoint**: `/users?page=1&pagesize=20&order=desc&sort=reputation&site=stackoverflow`
- **Error Handling**: Graceful handling of network timeouts and API failures
- **Data Parsing**: Manual JSON parsing for lightweight implementation

## Limitations

- Follow functionality is local-only (no actual API calls to StackOverflow)
- Limited to top 20 users (no pagination implemented)
- Search is client-side only (no server-side filtering)
- No user authentication or personalization features

## How to Run Tests

Run unit tests from Android Studio using the built-in test runner, or use the command line:
```bash
./gradlew test
```

## Project Structure

```
app/src/main/java/com/example/pixeltechtest/
├── data/
│   ├── model/          # Data models (User, BadgeCounts)
│   └── repository/     # Data layer (UserRepository)
├── di/                 # Dependency injection modules
├── ui/
│   ├── compose/        # Composable screens and components
│   ├── navigation/     # Navigation setup and routing
│   └── viewmodel/      # ViewModels for state management
└── MainActivity.kt     # Main activity and app setup
```

## Why These Choices?

- **DataStore over SharedPreferences**: Type-safe, asynchronous, and modern
- **Hilt over Manual DI**: Reduces boilerplate and provides compile-time safety
- **Navigation Component**: Handles complex navigation flows and deep linking
- **Material 3**: Latest design system with improved accessibility and theming
- **Jetpack Compose**: Modern, declarative UI with less boilerplate than XML layouts
- **MVVM Architecture**: Testable, maintainable, and follows Android best practices
