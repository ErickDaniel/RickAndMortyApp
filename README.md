# Rick and Morty App

Android application that displays a list of characters from the Rick and Morty API.

## Features

- Displays a list of Rick and Morty characters
- Shows character name, status and image
- Handles loading, success and error states
- Allows retrying when an error occurs

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM
- Coroutines
- Retrofit
- Coil
- JUnit

## Architecture

The application follows an MVVM architecture and separates responsibilities
between the UI, domain and data layers.

## Testing

Unit tests are included for the ViewModel to validate the different UI states
and repository interactions.

## How to Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the Gradle dependencies.
4. Run the application on an emulator or physical device.

## Technical desitions

### Architecture
The application Follows the MVVM architecture with a Clean Layered structure
that separates UI, Domain and Data responsibilities.

### UI State

The screen is driven by a single UI state exposed by the ViewModel.

The possible states of the screen are explicitly modeled, allowing the UI to react predictably
to loading, success, and error scenarios.

Jetpack Compose observes this state and renders the appropriate content without containing
business logic.

### Repository

The Repository is used between the ViewModel and the data source. This keeps the presentation
layer independent of the networking implementation and makes it easier to test.

### Data and Domain Models

API response models (DTOs) are separated from the models used by the rest of the application.

### Dependency Injection

Dependencies are provided explicitly instead of being instantiated inside the ViewModel.

A custom `ViewModelProvider.Factory` is used to provide the repository to the ViewModel. 
For the current scope of the project, this keeps dependency management simple without introducing an 
additional dependency injection framework.

For a larger application, this approach could be replaced by a dependency injection solution
such as Hilt.

### Coroutines

Kotlin Coroutines are used for asynchronous operations.

### Error Handling

Network and data errors are handled outside the UI.

### Image Loading

Coil is used for asynchronous image loading because it integrates naturally with Jetpack Compose

## Future Improvements

Given more time and a larger project scope, the following improvements could be considered:

- Dependency Injection
- Pagination
- Navigation and Character Details
- Improved Error Handling
- Dependency and Configuration Management
- Testing
- Improving UI and UX with Pull to refresh
- Offline Support (BD)
- Observability Crash reports
## AI Usage

AI tools were used during the development of this project as a supporting resource rather than as a 
replacement for the development process.

The main uses of AI included:

- Discussing architectural decisions and possible implementation approaches.
- Reviewing code and identifying potential improvements.
- Helping troubleshoot specific errors during development.
- Suggesting test scenarios and edge cases.
- Reviewing documentation and improving the clarity of the README.
- Acting as a second pair of eyes when evaluating implementation decisions.

The project was developed incrementally, with each component implemented and validated before moving
to the next step. AI suggestions were reviewed and adapted when necessary instead of being 
incorporated blindly.

This approach allowed AI to work similarly to a development assistant or code reviewer while keeping
the implementation decisions, understanding of the code, and final responsibility with the developer.