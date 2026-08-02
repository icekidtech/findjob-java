# FindJob - Job Board Platform

A comprehensive job board platform connecting freelancers with clients, featuring skill endorsements, project bidding, reviews, and reputation management.

## Technology Stack

- **Backend**: Spring Boot 3.2.0 with Java 11+
- **Frontend**: Thymeleaf + Bootstrap 5
- **Database**: PostgreSQL (with H2 support for development)
- **Build Tool**: Maven 3.6+
- **Security**: Spring Security 6 + JWT

## Prerequisites

- **Java**: JDK 11 or higher (tested with Java 26)
- **Maven**: 3.6.0 or higher
- **PostgreSQL**: 12+ (or use H2 for development)
- **Git**: For version control

### Installing Maven

If Maven is not installed, you can:

1. **Windows**: Download from [Apache Maven](https://maven.apache.org/download.cgi) and add to PATH
2. **Using Homebrew (macOS)**: `brew install maven`
3. **Using apt (Linux)**: `sudo apt-get install maven`
4. **Using Windows Package Manager**: `winget install Maven.Maven`

Verify installation:
```bash
mvn --version
```

## Project Structure

```
findjob/
├── src/main/
│   ├── java/com/findjob/jobboard/
│   │   ├── controller/          # REST/Web controllers
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Data access layer (JPA)
│   │   ├── model/               # Entity models
│   │   ├── dto/                 # Data transfer objects
│   │   └── FindJobApplication.java  # Main entry point
│   └── resources/
│       ├── templates/           # Thymeleaf HTML templates
│       ├── static/              # CSS, JS, images
│       └── application.properties  # Configuration
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/findjob.git
cd findjob
```

### 2. Database Setup

#### Option A: PostgreSQL (Production)

```bash
# Create database
createdb findjob

# Update database credentials in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/findjob
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### Option B: H2 (Development)

No setup required! Use the H2 profile for instant local development:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Access H2 Console at: http://localhost:8080/h2-console

### 3. Build the Project

```bash
# Clean and build
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl :findjob-jobboard
```

### 4. Run the Application

#### Using Maven
```bash
# Development (default - uses PostgreSQL)
mvn spring-boot:run

# Development with H2
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

#### Using JAR
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/findjob-jobboard-1.0.0.jar

# Run with H2 profile
java -jar target/findjob-jobboard-1.0.0.jar --spring.profiles.active=h2
```

### 5. Access the Application

- **Home Page**: http://localhost:8080
- **Login**: http://localhost:8080/auth/login
- **Register**: http://localhost:8080/auth/register
- **H2 Console** (dev): http://localhost:8080/h2-console

## Maven Commands

### Development
```bash
# Build without tests
mvn clean package -DskipTests

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Generate project documentation
mvn site
```

### IDE Integration
```bash
# Generate IDE configuration (IntelliJ IDEA)
mvn idea:idea

# Generate IDE configuration (Eclipse)
mvn eclipse:eclipse

# Clean IDE configuration
mvn idea:clean eclipse:clean
```

## Configuration

### application.properties

Main configuration file located at `src/main/resources/application.properties`:

- **Server**: Port 8080
- **Database**: PostgreSQL on localhost:5432
- **Thymeleaf**: Template caching disabled for development
- **Logging**: DEBUG level for com.findjob package

### Profiles

- **dev** (default): Development with live reload
- **h2**: In-memory H2 database for quick testing
- **prod**: Production settings

Set active profile:
```bash
# Environment variable
export SPRING_PROFILES_ACTIVE=h2

# Application properties
spring.profiles.active=h2

# Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# Java
java -jar app.jar --spring.profiles.active=h2
```

## Development Features

### Hot Reload
With DevTools enabled, changes to files trigger automatic restart:
```bash
# Just save your file and the app reloads!
```

### Debugging
```bash
# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Dependencies

### Core Dependencies
- **spring-boot-starter-web**: REST & Web MVC
- **spring-boot-starter-data-jpa**: Database ORM
- **spring-boot-starter-security**: Authentication & Authorization
- **spring-boot-starter-thymeleaf**: Template engine
- **spring-boot-starter-validation**: Bean validation
- **lombok**: Reduces boilerplate code
- **mapstruct**: Object mapping
- **jjwt**: JWT token support

### Database
- **postgresql**: PostgreSQL driver
- **mysql-connector-j**: MySQL driver (optional)
- **h2**: In-memory database for development

### Testing
- **spring-boot-starter-test**: JUnit 5, Mockito, AssertJ
- **spring-security-test**: Security testing utilities

See `pom.xml` for complete dependency list.

## Troubleshooting

### Maven: command not found
```bash
# Add Maven to PATH or use full path
C:\Maven\bin\mvn --version

# Or install Maven via package manager
# Windows: winget install Maven.Maven
# macOS: brew install maven
# Linux: sudo apt-get install maven
```

### Database Connection Failed
```bash
# Check PostgreSQL is running
# Update database credentials in application.properties
# Or use H2 profile: --spring.profiles.active=h2
```

### Port 8080 Already in Use
```bash
# Change port in application.properties
server.port=8081

# Or via command line
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Tests Failing
```bash
# Run tests with more verbose output
mvn test -e -X

# Skip tests
mvn clean install -DskipTests
```

## Project Features

### Phase 1: Authentication
- User registration (Freelancer/Client roles)
- Login with session management
- Profile role selection
- CSRF protection

### Phase 2: User Management
- Complete user profiles
- Profile editing and viewing
- Role-based dashboards
- Tier system (Beginner/Intermediate/Expert)

### Phase 3: Job Management
- Job posting and browsing
- Job filtering by skills
- Freelancer bidding
- Application status tracking

### Phase 4: Endorsements & Reviews
- Skill endorsements (peer/client/verified)
- Post-project reviews
- Rating system (1-5 stars)
- Reputation calculation

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout

### Users
- `GET /profile/{id}` - View user profile
- `GET /profile/me` - View own profile
- `POST /profile/complete` - Complete profile
- `PUT /profile/edit` - Edit profile

### Jobs
- `GET /jobs` - List all jobs
- `GET /jobs/{id}` - View job details
- `POST /jobs` - Create new job
- `PUT /jobs/{id}` - Update job

### Applications
- `POST /applications` - Submit bid
- `GET /applications/user/{userId}` - View applications

### Endorsements
- `POST /endorsements` - Create endorsement
- `GET /endorsements/user/{userId}` - View endorsements
- `DELETE /endorsements/{id}` - Revoke endorsement

### Reviews
- `POST /reviews` - Submit review
- `GET /reviews/user/{userId}` - View reviews

## Contributing

1. Create a feature branch: `git checkout -b feature/amazing-feature`
2. Commit changes: `git commit -m 'Add amazing feature'`
3. Push to branch: `git push origin feature/amazing-feature`
4. Open a pull request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Contact

For questions or support, please reach out to the development team.

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [PostgreSQL](https://www.postgresql.org/docs/)
