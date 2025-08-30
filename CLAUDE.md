# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a KYC (Know Your Customer) system that provides identity verification services through document verification and face recognition. The system consists of:

- **Backend**: Spring Boot application with MongoDB
- **Frontend**: Android app (Kotlin) + React TypeScript admin console  
- **Third-party integrations**: Async document and face verification services

## Architecture

### Core Components
- **KYC Process Engine**: Manages the verification workflow
- **Workflow Service**: Configurable workflow execution with async third-party service calls
- **File Management**: Handles document/image uploads
- **Admin Console API**: Management and monitoring endpoints

### Key Models
- `KycProcess`: Main entity tracking verification status and workflow progress
- `WorkflowConfig`: Configurable workflow definitions
- `WorkflowNode`: Individual verification steps (ID verification, face verification, deepfake detection)

## Development Commands

### Backend (Spring Boot)
```bash
mvn clean compile                    # Compile the project
mvn spring-boot:run                 # Run the application (port 8080)
mvn clean package                   # Build JAR file
java -jar target/kyc-1.0-SNAPSHOT.jar  # Run the JAR
mvn test                            # Run all tests
mvn test -Dtest=KycServiceTest      # Run specific test class
```

### Web Console (React + TypeScript)
```bash
cd web-console                      # Navigate to web console directory
npm install                         # Install dependencies
npm start                          # Start development server (port 3000)
npm run build                      # Build for production
npm test                           # Run tests
```

### Android App (Kotlin)
- Open `android/` directory in Android Studio
- Sync Gradle dependencies
- Connect Android device or start emulator
- Run application from Android Studio

### Database
- MongoDB runs on localhost:27017
- Database: `kyc_db` (production), `kyc_db_test` (testing)
- Default workflow configuration is auto-initialized on startup

## Key APIs

### Client APIs (Android)
- `POST /api/kyc/upload-id-card` - Upload ID document
- `POST /api/kyc/{kycId}/upload-face` - Upload face photo  
- `GET /api/kyc/{kycId}/status` - Poll verification status

### Admin APIs (React Console)
- `GET /api/admin/kyc` - List all KYC processes
- `GET /api/admin/kyc/{kycId}` - Get process details
- `DELETE /api/admin/kyc/{kycId}` - Delete process

### Third-party Callbacks
- `POST /api/callback/id-verification` - ID verification results
- `POST /api/callback/face-verification` - Face verification results

## Workflow System

The system uses a flexible workflow engine:

1. **Default Workflow**: ID verification → Face verification  
2. **Extensible**: Can add nodes like deepfake detection
3. **Async Processing**: All third-party calls are non-blocking
4. **Callback Driven**: Results received via webhooks

### Adding New Verification Nodes
1. Add new `NodeType` enum value in `KycProcess`
2. Update `WorkflowService.executeNode()` to handle the new type
3. Add corresponding third-party service method in `ThirdPartyService`
4. Create callback handler in `ThirdPartyCallbackService`

## File Structure

### Backend
```
src/main/java/org/example/
├── KycApplication.java          # Spring Boot main class
├── config/                      # Configuration classes
├── controller/                  # REST API controllers
├── dto/                        # Data transfer objects
├── model/                      # MongoDB entities
├── repository/                 # Data access layer
└── service/                    # Business logic
```

### Frontend - Android App
```
android/app/src/main/java/com/example/kycapp/
├── MainActivity.kt              # Application home screen
├── KycProcessActivity.kt        # Main KYC verification flow
├── StatusActivity.kt           # KYC status polling screen
├── data/
│   ├── model/KycModels.kt      # Data classes
│   └── api/                    # Network layer
│       ├── ApiClient.kt        # Retrofit configuration
│       └── KycApiService.kt    # API service interface
└── res/layout/                 # XML layout files
```

### Frontend - Web Console  
```
web-console/src/
├── App.tsx                     # Main application component
├── types/index.ts              # TypeScript type definitions
├── services/api.ts             # API service layer
├── components/
│   └── Layout.tsx             # Main layout component
└── pages/
    ├── Dashboard.tsx          # Statistics dashboard
    ├── KycList.tsx           # KYC processes list
    └── KycDetail.tsx         # KYC process detail view
```

## Configuration

Key configuration in `application.yml`:
- MongoDB connection settings
- File upload limits and paths
- Third-party service URLs and timeouts
- Callback URL configuration

## Development Notes

### Backend
- Uses async processing with `@Async` for workflow execution
- File uploads stored in `uploads/` directory
- Lombok used for reducing boilerplate code
- WebClient for reactive HTTP calls to third-party services
- Comprehensive error handling and logging

### Android Frontend
- **Technology Stack**: Kotlin, Retrofit, CameraX, Glide, Dexter
- **Key Features**:
  - Camera integration for document and face photo capture
  - Async file upload with progress tracking
  - Real-time status polling every 3 seconds
  - Permission handling for camera and storage
  - Material Design UI components
- **API Base URL**: Configure in `ApiClient.kt` (default: `http://10.0.2.2:8080` for emulator)

### Web Console Frontend  
- **Technology Stack**: React 18, TypeScript, Ant Design, Axios, React Router
- **Key Features**:
  - Responsive dashboard with real-time statistics
  - KYC process list with filtering and pagination
  - Detailed process view with timeline visualization
  - CRUD operations for KYC management
  - Modern UI with Ant Design components
- **Development**: Runs on port 3000, proxies API calls to backend port 8080

### Integration Points
- Android app uploads files to `/api/kyc/upload-*` endpoints
- Web console manages data through `/api/admin/kyc/*` endpoints  
- Third-party services send callbacks to `/api/callback/*` endpoints
- Real-time status updates via polling (Android) and refresh (Web)

### Common Development Tasks
1. **Adding new workflow nodes**: Update backend enum, service, and both frontends
2. **UI changes**: Android uses XML layouts, Web uses React components
3. **API changes**: Update service interfaces in both Android and Web clients
4. **Styling**: Android uses Material Design, Web uses Ant Design
5. **Testing**: Backend with JUnit, Android with Espresso, Web with Jest