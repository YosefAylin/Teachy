# 🎓 Teachy – Online Learning Platform

<p align="center">
  <img src="docs/images/TEACHY.png" alt="Teachy Logo" width="250" height="250"/>
</p>

---

## 📖 Project Overview
Teachy is an **online learning platform** developed as part of an academic project.  
It is built with **Spring Boot, PostgreSQL, Docker, and CI/CD pipelines**.  
The platform allows **students and teachers** to interact, schedule lessons, and manage educational content in a secure and scalable way.

---

## 🏗️ Architecture
The project follows the **MVC (Model–View–Controller)** architectural pattern:

- **Model** → JPA Entities (Student, Teacher, Course, Lesson, Message, etc.)
- **View** → Initially Thymeleaf, later migrating toward a modern front-end stack
- **Controller** → REST APIs and web controllers, handling user requests

### UML Diagrams

#### Class Diagram
<p align="center">
  <img src="docs/images/classDiagram.png" alt="Class Diagram" width="500"/>
</p>

#### Sequence Diagrams
<p align="center">
  <img src="docs/images/sequence1.png" alt="Sequence Diagram 1" width="400"/>
  <img src="docs/images/sequence2.png" alt="Sequence Diagram 2" width="400"/>
  <img src="docs/images/sequence3.png" alt="Sequence Diagram 3" width="400"/>
</p>

#### Use Case Model
[📄 Use Case Model](docs/useCaseModel.pdf)

#### MVC
[📄 MVC](docs/MVC.pdf)



---

## 🎨 Design Patterns

We applied several **Design Patterns** to improve maintainability and scalability:

- **Factory Pattern**  
  Used in user registration to create `Student` or `Teacher` objects dynamically depending on the role.  
  This keeps the creation logic centralized and easier to extend.

- **Facade Pattern**  
  Provides a unified interface (`UserFacade`) to multiple subsystems (authentication, profile, lessons).  
  It hides complexity and simplifies interactions for higher-level modules.

- **Cache Pattern (LRU)**  
  Implemented an **LRU Cache** to optimize repeated queries (e.g., lessons or teacher info).  
  This reduces DB load and improves performance.

---

## 🧪 Testing

The project includes a **comprehensive testing setup** to ensure reliability:

- **Unit Tests (JUnit + Spring Boot Test)**  
  Validate individual services, repositories, and controllers.  
  Example: testing the `UserFactory` ensures correct creation of `Student` vs. `Teacher`.

- **Integration Tests**  
  Run with an **H2 in-memory database** (`spring.profiles.active=test`)  
  → isolates the test environment from production PostgreSQL.  

- **Coverage**  
  - User registration & login (Factory + Facade).  
  - Lesson scheduling & teacher/student interactions.  
  - Cache behavior (hit/miss + eviction strategy).  
  - Role-based access control & security.  

All tests run automatically on **GitHub Actions CI**.  

---

## ⚙️ CI/CD Pipeline

We implemented **GitHub Actions** for automated workflows:

- **CI (Continuous Integration)**  
  - Runs on every push/PR to `main`.  
  - Builds with Maven.  
  - Runs all unit + integration tests.  
  - Uploads test reports as artifacts.  

- **CD (Continuous Deployment)**  
  - Packages the app into a `.jar`.  
  - Builds a **Docker image**.  
  - Pushes image to **Docker Hub** with two tags:  
    - `latest`  
    - commit-specific `${GITHUB_SHA}`  

This ensures the platform is always **buildable, testable, and deployable**.  

---

## 🐳 Docker & Deployment

The platform is fully containerized with **Docker Compose**.  

### Services
- **App** → Spring Boot service (`online-learning-app`).  
- **Database** → PostgreSQL (`teachy-db`).  
- **Admin Tool** → pgAdmin (`teachy-pgadmin`).  

### Example Commands
```bash
# Build images
docker compose build

# Run services
docker compose up -d

# Check running containers
docker compose ps
