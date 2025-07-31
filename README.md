# Teachy - Online Learning Platform

<p align="center">
  <img width="300" height="320" alt="Teachy Logo" src="https://github.com/user-attachments/assets/7e42ff55-a549-4fa2-b608-c94af927c522" />
</p>

## Overview

Teachy is a comprehensive online learning platform that connects students with teachers in a virtual classroom environment. The platform facilitates course management, student enrollment, and educational content delivery through a modern, user-friendly interface.

## Features

- **User Management**
  - Role-based authentication (Student, Teacher, Admin)
  - User registration and login
  - Profile management

- **Course Management**
  - Course creation and editing (for Teachers)
  - Lesson scheduling
  - Course enrollment (for Students)

- **Learning Experience**
  - Access to course materials
  - Progress tracking
  - Reviews and ratings

- **Administrative Tools**
  - User management
  - Platform settings
  - Analytics and reporting

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.3
- **Frontend**: Thymeleaf, HTML, CSS
- **Database**: H2 (development), PostgreSQL (production)
- **Security**: Spring Security
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/online-learning-platform.git
   cd online-learning-platform
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   Open your browser and navigate to `http://localhost:8080`

### Default Credentials

The system is initialized with the following default users:
- Admin: Username defined in application properties
- Test accounts can be created through the registration page

## Project Structure

The project follows a standard Spring Boot application structure with clear separation of concerns:

```
src/main/java/io/jos/onlinelearningplatform/
├── config/                  # Configuration classes
├── controller/              # REST controllers by domain
├── dto/                     # Data Transfer Objects
├── model/                   # Domain models/entities
├── repository/              # Data access layer
├── service/                 # Business logic
└── util/                    # Utility classes
```

## API Documentation

The application provides RESTful endpoints for various operations. Key endpoints include:

- **Authentication**
  - `GET /login` - Login page
  - `GET /register` - Registration page
  - `POST /register` - Process registration

- **Home**
  - `GET /` - Home page
  - `GET /home/redirect` - Role-based redirection

- **Role-Specific Pages**
  - `GET /student/home` - Student dashboard
  - `GET /teacher/home` - Teacher dashboard
  - `GET /admin/home` - Admin dashboard

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- All contributors who have helped shape this project

---

*Last updated: July 31, 2025*
