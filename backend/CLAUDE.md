# CLAUDE.md - KYC Backend Module

This file provides guidance to Claude Code when working with the KYC backend module.

## Module Overview

Spring Boot backend application providing REST APIs for KYC (Know Your Customer) verification services. Handles document verification, face recognition, and workflow management through integration with third-party verification services.

## Technology Stack

- **Framework**: Spring Boot 2.7.0
- **Java Version**: 11
- **Database**: MongoDB
- **Build Tool**: Maven
- **Key Dependencies**:
  - Spring Web (REST APIs)
  - Spring Data MongoDB (Data persistence) 
  - Spring WebFlux (Reactive HTTP client)
  - Spring Validation (Request validation)
  - Spring AOP & Actuator (Monitoring)
  - Lombok (Code generation)
  - Jackson (JSON processing)

## Development Commands

```bash
# Compile and build
mvn clean compile                    # Compile the project
mvn clean package                   # Build JAR file

# Run application
mvn spring-boot:run                 # Run in development mode (port 8080)
java -jar target/kyc-1.0-SNAPSHOT.jar  # Run JAR file

# Testing
mvn test                            # Run all tests
mvn test -Dtest=KycServiceTest      # Run specific test class
```

## Project Structure

```
src/main/java/org/example/
├── KycApplication.java              # Main application class (@SpringBootApplication, @EnableAsync)
├── config/                          # Configuration classes
│   ├── DataInitializer.java        # Database initialization
│   └── WebConfig.java              # Web configuration (CORS, etc.)
├── controller/                      # REST API endpoints
│   ├── KycController.java          # Client-facing KYC APIs
│   ├── KycAdminController.java     # Admin management APIs
│   └── CallbackController.java     # Third-party service callbacks
├── dto/                            # Data Transfer Objects
│   ├── ApiResponse.java            # Standard API response wrapper
│   ├── CreateKycSessionRequest.java # Session creation request
│   ├── CreateKycSessionResponse.java # Session creation response
│   ├── KycStatusResponse.java      # Status polling response
│   ├── KycUploadRequest.java       # File upload request
│   ├── KycUploadResponse.java      # File upload response
│   └── ThirdPartyCallbackRequest.java # Callback payload
├── model/                          # MongoDB entities
│   ├── KycProcess.java             # Main KYC process entity
│   └── WorkflowConfig.java         # Workflow configuration entity
├── repository/                     # Data access layer
│   ├── KycProcessRepository.java   # KycProcess CRUD operations
│   └── WorkflowConfigRepository.java # Workflow configuration CRUD
└── service/                        # Business logic
    ├── KycService.java             # Core KYC business logic
    ├── KycAdminService.java        # Admin operations
    └── ThirdPartyCallbackService.java # Callback processing
```

## Configuration

**application.yml** key settings:
- **Server**: Port 8080
- **MongoDB**: localhost:27017, database `kyc_db`
- **File Upload**: Max 10MB files, stored in `uploads/` directory
- **Third-party Services**: Configurable URLs and timeouts
- **Logging**: DEBUG level for application and MongoDB queries
- **Actuator**: Health, info, and metrics endpoints enabled

## Core APIs

### Client APIs (for Android app)
- `POST /api/kyc/session` - Create new KYC session
- `POST /api/kyc/upload-id-card` - Upload ID document
- `POST /api/kyc/{kycId}/upload-face` - Upload face photo
- `GET /api/kyc/{kycId}/status` - Poll verification status

### Admin APIs (for web console)
- `GET /api/admin/kyc` - List all KYC processes
- `GET /api/admin/kyc/{kycId}` - Get specific process details
- `DELETE /api/admin/kyc/{kycId}` - Delete KYC process

### Third-party Callbacks
- `POST /api/callback/id-verification` - Receive ID verification results
- `POST /api/callback/face-verification` - Receive face verification results

## Key Models

### KycProcess
Main entity tracking the verification workflow state:
- Stores process status, workflow progress, and uploaded files
- Contains workflow node execution history
- Tracks third-party service call results

### WorkflowConfig  
Configurable workflow definitions:
- Defines verification steps and their sequence
- Supports different workflow types (standard, enhanced, etc.)
- Auto-initialized with default configuration on startup

## Business Logic

### Async Workflow Processing
- Uses `@Async` for non-blocking third-party service calls
- Workflow execution managed by `WorkflowService`
- Results processed via webhook callbacks
- Status updates stored in MongoDB

### File Management
- Supports ID documents and face photos
- File validation and size limits enforced
- Secure file storage with organized directory structure

### Error Handling
- Comprehensive validation using Spring Validation
- Standardized error responses via `ApiResponse`
- Detailed logging for debugging and monitoring

## Database

- **Production**: `kyc_db` on localhost:27017
- **Testing**: `kyc_db_test` (using embedded MongoDB for tests)
- **Collections**: `kyc_processes`, `workflow_configs`
- **Initialization**: Default workflow config created on startup

## Development Notes

### Adding New Features
1. Create DTOs in `dto/` package for request/response objects
2. Add business logic to appropriate service class
3. Create REST endpoint in relevant controller
4. Update models if database changes needed
5. Write tests for new functionality

### Third-party Integration
- All external calls use Spring WebFlux WebClient
- Timeout and retry policies configured in application.yml
- Callback handling for async result processing
- Mock services available for development/testing

### Testing Strategy
- Unit tests for service layer business logic
- Integration tests for repository layer
- Embedded MongoDB for test database
- Mock third-party services for reliable testing

## Common Tasks

### Debugging
- Check application logs for detailed error information
- Use actuator endpoints (`/actuator/health`) for system status
- MongoDB query logging enabled for database debugging

### Adding Workflow Nodes
1. Update `NodeType` enum in `KycProcess` model
2. Add processing logic in `WorkflowService.executeNode()`
3. Create corresponding third-party service method
4. Add callback handler in `ThirdPartyCallbackService`

### Configuration Changes
- Update `application.yml` for environment-specific settings
- Restart application to apply configuration changes
- Use Spring profiles for different environments