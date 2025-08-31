# CLAUDE.md - Android KYC App

This file provides guidance to Claude Code when working with the Android KYC application module.

## Module Overview

This is the Android client application for the KYC (Know Your Customer) system. It provides a native mobile interface for users to complete identity verification through document capture and face recognition.

## Technology Stack

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with View Binding and Data Binding
- **Network**: Retrofit2 + OkHttp3 + Gson
- **Camera**: CameraX
- **Image Loading**: Glide
- **Permissions**: Dexter
- **UI**: Material Design Components

## Project Structure

```
app/src/main/
├── java/com/example/kycapp/
│   ├── MainActivity.kt                 # Home screen with navigation buttons
│   ├── KycProcessActivity.kt          # Main KYC verification workflow
│   ├── StatusActivity.kt              # KYC status checking and polling
│   └── data/
│       ├── api/
│       │   ├── ApiClient.kt           # Retrofit configuration and client setup
│       │   └── KycApiService.kt       # API service interface definitions
│       └── model/
│           └── KycModels.kt           # Data classes for API requests/responses
├── res/
│   ├── layout/                        # XML layout files for activities
│   ├── values/                        # Colors, strings, themes
│   └── mipmap-*/                      # App icons
└── AndroidManifest.xml                # App permissions and activity declarations
```

## Key Features

### Core Functionality
- **Document Capture**: Camera integration for ID card photography
- **Face Capture**: Selfie capture for face verification
- **File Upload**: Async multipart file upload with progress tracking
- **Status Polling**: Real-time KYC process status monitoring (3-second intervals)
- **Permission Handling**: Camera and storage permissions with Dexter

### Activities
- **MainActivity**: Entry point with "Start KYC" and "Check Status" options
- **KycProcessActivity**: Complete KYC workflow including document and face capture
- **StatusActivity**: Real-time status monitoring with automatic polling
- **CameraActivity**: Camera interface for document/face capture (declared in manifest)

## Development Commands

### Build and Run
```bash
# Open in Android Studio
# File -> Open -> select /android directory

# Build project
./gradlew build

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

### Gradle Tasks
```bash
# List all tasks
./gradlew tasks

# Lint check
./gradlew lint

# Generate signed APK
./gradlew assembleRelease
```

## Configuration

### API Configuration
- **Base URL**: Configure in `ApiClient.kt:10`
  - Emulator: `http://10.0.2.2:8080/`
  - Real device: `http://YOUR_SERVER_IP:8080/`
- **Timeout Settings**: 30 seconds for connect/read/write operations
- **Logging**: Full HTTP body logging enabled in debug builds

### Required Permissions
- `INTERNET`: API communication
- `CAMERA`: Document and face photo capture
- `READ_EXTERNAL_STORAGE`: Image file access
- `WRITE_EXTERNAL_STORAGE`: Image file storage

### Network Security
- **Cleartext Traffic**: Enabled for development (HTTP connections)
- **Production**: Disable cleartext traffic and use HTTPS only

## Key APIs Integration

### KYC Workflow APIs
- `POST /api/kyc/upload-id-card`: Upload identity document
- `POST /api/kyc/{kycId}/upload-face`: Upload face photo
- `GET /api/kyc/{kycId}/status`: Poll verification status

### API Models
All request/response models defined in `KycModels.kt`:
- `KycUploadResponse`: Server response with KYC ID
- `KycStatusResponse`: Current verification status and results
- Status polling every 3 seconds until completion or failure

## Development Guidelines

### Code Style
- **Kotlin Conventions**: Follow official Kotlin coding conventions
- **View Binding**: Use view binding for all layout interactions
- **Data Binding**: Enabled for complex UI data binding scenarios
- **Parcelize**: Use `@Parcelize` for data classes passed between activities

### Dependencies Management
- **Core Libraries**: AndroidX components (Core, AppCompat, Material)
- **Architecture**: Activity/Fragment KTX, Navigation, Lifecycle
- **Network**: Retrofit2 ecosystem with logging interceptor
- **UI**: Material Design 3 components
- **Camera**: CameraX for modern camera operations

### Error Handling
- Network errors with retry mechanisms
- Permission denied scenarios
- Camera access failures
- File upload progress and error states

### Testing
- **Unit Tests**: JUnit 4 for business logic
- **UI Tests**: Espresso for activity and user flow testing
- **Integration Tests**: API communication testing

## Common Development Tasks

### Adding New API Endpoints
1. Define interface methods in `KycApiService.kt`
2. Add request/response models to `KycModels.kt`
3. Implement API calls in activity/service classes
4. Handle success/error responses appropriately

### UI Modifications
1. Update XML layouts in `res/layout/`
2. Modify activity classes for new UI interactions
3. Update view binding references
4. Test on different screen sizes and orientations

### Camera Integration Changes
1. Modify CameraX configuration for different use cases
2. Update image processing and compression logic
3. Handle different camera orientations and resolutions
4. Test on various device camera capabilities

### Permission Updates
1. Add new permissions to `AndroidManifest.xml`
2. Update Dexter permission requests in activities
3. Handle permission denied scenarios gracefully
4. Provide clear user feedback for permission requirements

## Build Configuration

### App Module (`app/build.gradle`)
- **Application ID**: `com.example.kycapp`
- **Version**: 1.0 (versionCode: 1)
- **Compile SDK**: 34
- **Build Features**: View Binding and Data Binding enabled
- **Proguard**: Disabled in debug, configure for release builds

### Dependencies
- **Kotlin**: Latest stable version with parcelize plugin
- **AndroidX**: Core, AppCompat, Material Design, Navigation
- **Network**: Retrofit 2.9.0, OkHttp 4.12.0
- **Camera**: CameraX 1.3.1
- **Image**: Glide 4.16.0
- **Permissions**: Dexter 6.2.3

## Deployment Notes

### Debug Builds
- HTTP traffic allowed for development
- Full logging enabled
- Debug keystore signing

### Release Builds
- Configure ProGuard/R8 for code obfuscation
- Disable HTTP logging
- Use production API endpoints
- Sign with release keystore
- Enable cleartext traffic restrictions

### Device Testing
- Test on various Android versions (API 24+)
- Verify camera functionality across different devices
- Test network connectivity on real devices
- Validate file upload performance on different connection speeds

## Integration Points

- **Backend API**: Communicates with Spring Boot backend on port 8080
- **File Upload**: Multipart form data for document/image uploads
- **Status Polling**: Continuous status checking until process completion
- **Error Handling**: Graceful degradation for network/server issues