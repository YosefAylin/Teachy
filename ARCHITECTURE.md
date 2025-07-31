# Teachy Architecture Documentation

This document provides an overview of the architecture of the Teachy Online Learning Platform.

## System Architecture

Teachy follows a standard layered architecture pattern commonly used in Spring Boot applications:

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  (Controllers, Thymeleaf Templates, HTML/CSS/JavaScript) │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     Service Layer                        │
│         (Business Logic, Service Implementations)        │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  Data Access Layer                       │
│               (Repositories, Entities)                   │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      Database                            │
│                (H2/PostgreSQL)                           │
└─────────────────────────────────────────────────────────┘
```

### Presentation Layer

The presentation layer handles user interactions and is responsible for:
- Processing HTTP requests and responses
- Rendering views using Thymeleaf templates
- Implementing controllers for different functional areas
- Managing user authentication and authorization

Key components:
- `controller/auth/AuthController.java`: Handles user authentication
- `controller/home/HomeController.java`: Manages home page rendering
- `controller/admin/AdminController.java`: Admin-specific functionality
- `controller/teacher/TeacherController.java`: Teacher-specific functionality
- `controller/student/StudentController.java`: Student-specific functionality

### Service Layer

The service layer contains the business logic of the application and is responsible for:
- Implementing business rules and workflows
- Coordinating between controllers and repositories
- Managing transactions
- Handling data validation and transformation

Key components:
- `service/UserService.java`: Interface for user-related operations
- `service/impl/UserServiceImpl.java`: Implementation of user service
- Other service interfaces and implementations for specific domains

### Data Access Layer

The data access layer manages interactions with the database and is responsible for:
- Defining entity models
- Implementing repositories for CRUD operations
- Managing database connections and transactions

Key components:
- `model/User.java`: Base user entity
- `model/Student.java`, `model/Teacher.java`, `model/Admin.java`: User role entities
- `model/Lesson.java`, `model/Schedule.java`, etc.: Domain entities
- `repository/UserRepository.java`: Repository for user data access

### Cross-Cutting Concerns

Several components address cross-cutting concerns:

#### Security

Security is managed through Spring Security:
- `config/SecurityConfig.java`: Configures authentication, authorization, and security rules
- Role-based access control for different user types
- Password encryption using BCrypt

#### Configuration

Application configuration is managed through:
- `config/AdminInitializer.java`: Initializes admin user on startup
- `application.properties`: Contains application settings

## Data Model

The core data model revolves around users, courses, and learning activities:

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │       │   Lesson    │       │  Schedule   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id          │       │ id          │       │ id          │
│ username    │       │ title       │       │ startTime   │
│ email       │       │ description │       │ endTime     │
│ passwordHash│       │ content     │       │ lessonId    │
│ connected   │       │ teacherId   │       │             │
└─────────────┘       └─────────────┘       └─────────────┘
      ▲
      │
      │
┌─────┴─────┐
│           │
▼           ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   Student   │ │   Teacher   │ │    Admin    │
├─────────────┤ ├─────────────┤ ├─────────────┤
│ (User props)│ │ (User props)│ │ (User props)│
│             │ │             │ │             │
└─────────────┘ └─────────────┘ └─────────────┘
```

### Inheritance Strategy

The application uses a single-table inheritance strategy for the User entity hierarchy:
- `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`
- `@DiscriminatorColumn(name = "user_type")`

This approach simplifies queries while maintaining the object-oriented structure.

## Authentication Flow

The authentication process follows these steps:

1. User submits credentials via the login form
2. Spring Security processes the authentication request
3. UserDetailsService loads the user from the database
4. Password encoder verifies the password
5. On success, the user is redirected to their role-specific dashboard
6. On failure, the user is redirected to the login page with an error message

## Request Processing Flow

A typical request in the application follows this flow:

1. Client sends HTTP request
2. Spring Security filter chain processes authentication/authorization
3. Spring MVC routes the request to the appropriate controller
4. Controller calls service methods to execute business logic
5. Service interacts with repositories to access data
6. Repository performs database operations
7. Results flow back up through the layers
8. Controller prepares the model and selects the view
9. Thymeleaf renders the HTML response
10. Response is sent back to the client

## Technology Choices

### Spring Boot

Spring Boot was chosen as the primary framework for:
- Rapid development with convention over configuration
- Comprehensive ecosystem for web applications
- Robust security features
- Excellent integration with other technologies

### Thymeleaf

Thymeleaf was selected as the template engine because:
- Natural integration with Spring Boot
- Server-side rendering for improved SEO
- Natural templating that works as static prototypes
- Good support for security integration

### H2/PostgreSQL

The application uses:
- H2 for development and testing (in-memory database)
- PostgreSQL for production (robust, scalable relational database)

## Future Architecture Considerations

As the application grows, several architectural improvements could be considered:

1. **Microservices**: Split the monolith into domain-specific services
2. **API Gateway**: Implement an API gateway for routing and cross-cutting concerns
3. **Caching**: Add Redis or similar for performance optimization
4. **Message Queue**: Implement asynchronous processing for non-critical operations
5. **Frontend Framework**: Consider adding React or Vue.js for more interactive UI components

---

*Last updated: July 31, 2025*