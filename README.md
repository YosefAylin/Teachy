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

---

## ⚙️ CI/CD Pipeline
The project uses **GitHub Actions** to make development and deployment easier:

- Every time code is pushed to GitHub, the system **automatically checks** that everything builds and tests pass.  
- The app is then **packaged and prepared for deployment**.  
- A **Docker image** is created and uploaded, so the app can run anywhere.

This means the project is always **ready to run and deploy** without extra setup.

---

## 🐳 Docker & Deployment
The whole platform is **containerized with Docker** to make setup simple:

- **App** → the main Spring Boot service.  
- **Database** → PostgreSQL database.  
- **Admin Tool** → pgAdmin for database management.  

To run the project locally:  
```bash
docker compose up -d
``` 

Then you can access: 
	
 •	The app at http://localhost:8080
	
 •	pgAdmin (database UI) at http://localhost:5050
