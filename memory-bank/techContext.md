# Technology Context

## Core Technologies

-   **Platform**: Android
-   **Build System**: Gradle (using `gradlew` wrapper)
-   **Version Control**: Git
-   **Git Hosting & CI/CD**: GitHub & GitHub Actions

## Development Environment (Assumed for CI)

-   **Java Development Kit (JDK)**: Version 11 (a common choice for Android builds, can be adjusted if needed)
-   **Android SDK**: 
    -   `compileSdkVersion`: 31
    -   `minSdkVersion`: 21
    -   `targetSdkVersion`: 31
    (Derived from `app/build.gradle`)

## Key Dependencies (Workflow)

-   `actions/checkout@v3`: To check out the repository code.
-   `actions/setup-java@v3`: To set up the JDK.
-   `android-actions/setup-android@v2` (or a similar action): To set up the Android SDK environment. (Note: I'll use a common one, `actions/setup-java` and manual SDK tools download is also an option, but dedicated Android actions are often simpler).
-   `actions/upload-artifact@v3`: To upload the generated APK. 