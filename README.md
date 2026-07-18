#  TravelBuddy — Carpooling Web Platform for Belarus

A diploma project developed for the Java Software Engineering course. TravelBuddy is a monolithic web application designed to help young travelers find companions for trips across Belarus and split travel expenses seamlessly.

##  Tech Stack
- **Backend:** Java 17, Spring Boot 4.1.0, Spring Data JPA, Spring Security (Session-based authentication)
- **AOP:** Aspect-Oriented Programming for cross-cutting logging functionality (`spring-aspects`)
- **Database:** PostgreSQL 18 (Relational database structure conforming to 3NF)
- **Frontend:** Thymeleaf template engine, Bootstrap 5, Google Fonts (Inter)
- **Testing:** JUnit 5, Mockito (Business logic test coverage >90%)

##  Architecture & Requirements Fulfilled
1. **Database Schema:** Designed and implemented 6 interrelated tables (`users`, `trips`, `trip_applications`, `trip_budgets`, `comments`, `reviews`) strictly adhering to the Third Normal Form (3NF).
2. **Security Layers:** Integrated robust Spring Security configuration utilizing `BCryptPasswordEncoder` for hashing sensitive user data and establishing a secure login/logout mechanism.
3. **Core Business Logic:** Developed production-ready registration and trip management services with seamless dynamic data binding via Thymeleaf web views.
4. **Cross-Cutting Concerns (AOP):** Implemented an automated proxy-based `LoggingAspect` to transparently trace service-layer operations without code duplication.
5. **Quality Assurance:** Covered core execution branches with high-quality Unit tests using Mockito stubs, yielding over 90% method and line coverage metrics.

##  Installation & Getting Started
1. Clone this repository to your local machine.
2. Open your PostgreSQL terminal/GUI and execute: `CREATE DATABASE travel_buddy;`.
3. Configure your local database credentials (username and password) inside `src/main/resources/application.properties`.
4. Run the application via your IDE using the main class `TravelbuddyApplication.java`.
5. Open your web browser and navigate to: `http://localhost:8080/trips`.
