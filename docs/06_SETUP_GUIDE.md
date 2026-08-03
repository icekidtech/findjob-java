# FindJob - Setup Guide

## Prerequisites

### Required Software
- **Java:** JDK 11+ (tested with Java 25)
- **Maven:** 3.6.0+ ([Apache Maven](https://maven.apache.org))
- **Git:** For version control
- **Database:** PostgreSQL 12+ (or use H2 for development)

### Installation Steps

#### 1. Install Java
**Windows:**
```bash
# Download from oracle.com or use Windows Package Manager
winget install Oracle.JDK.25

# Verify installation
java -version
javac -version
```

**macOS:**
```bash
brew install openjdk@25
```

**Linux:**
```bash
sudo apt-get install openjdk-25-jdk
```

#### 2. Install Maven
**Windows:**
```bash
winget install Maven.Maven
mvn --version  # Verify
```

**macOS:**
```bash
brew install maven
mvn --version
```

**Linux:**
```bash
sudo apt-get install maven
mvn --version
```

#### 3. Install PostgreSQL (Optional - Use H2 for Development)
**Windows:**
Download from [postgresql.org](https://www.postgresql.org/download/windows/)

**macOS:**
```bash
brew install postgresql@14
brew services start postgresql@14
```

**Linux:**
```bash
sudo apt-get install postgresql postgresql-contrib
```

## Project Setup

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/findjob.git
cd findjob
```

### 2. Configure Environment
Create `.env` file in project root:
```bash
# Database Configuration (PostgreSQL)
DATABASE_URL=jdbc:postgresql://localhost:5432/findjob
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# Application Settings
APP_PORT=8080
JWT_SECRET=your_secret_key_here_change_in_production
```

### 3. Build Project
```bash
# Full build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build specific module
mvn -pl findjob-jobboard clean install
```

### 4. Run Development Server

#### Option A: With H2 (Recommended for Development)
```bash
# No database setup needed
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# Application will start at http://localhost:8080
# H2 Console: http://localhost:8080/h2-console (if enabled)
```

#### Option B: With PostgreSQL
```bash
# Create database first
createdb findjob

# Run application
mvn spring-boot:run

# Application at http://localhost:8080
```

#### Option C: Using JAR
```bash
# Build JAR file
mvn clean package

# Run JAR
java -jar target/findjob-jobboard-1.0.0.jar

# With H2 profile
java -jar target/findjob-jobboard-1.0.0.jar --spring.profiles.active=h2
```

## IDE Configuration

### IntelliJ IDEA
1. Open project: File → Open → Select project folder
2. Maven automatically detects pom.xml
3. Wait for indexing to complete
4. Run → Edit Configurations → Add Spring Boot config
5. Select main class: `com.findjob.jobboard.FindJobApplication`
6. Set active profiles: `h2` for development
7. Run using green play button

### Eclipse
```bash
# Generate Eclipse configuration
mvn eclipse:eclipse

# Import in Eclipse: File → Import → Existing Projects into Workspace
```

### VS Code
1. Install extensions: Spring Boot Extension Pack, Maven for Java
2. Open project folder
3. Spring Boot Extension automatically detects application
4. Use command palette: Spring Boot: Start

## Database Setup

### H2 (Development - Recommended)
No setup required! The application creates the database automatically.

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/findjob`
- Username: `sa`
- Password: (leave blank)

### PostgreSQL (Production)
```bash
# Create database
createdb findjob

# Create user (optional)
createuser findjob_user -P

# Update application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/findjob
spring.datasource.username=findjob_user
spring.datasource.password=your_password

# Start application
mvn spring-boot:run
```

## Testing Application

### Access the Application
- **Home:** http://localhost:8080
- **Register:** http://localhost:8080/auth/register
- **Login:** http://localhost:8080/auth/login

### Test User Accounts
Create accounts through the registration form:
- **Freelancer Account:** email, password, select "Freelancer" role
- **Client Account:** email, password, select "Client" role

### Test Workflows
1. **Register & Complete Profile**
   - Register as freelancer/client
   - Complete profile with skills/company info
   
2. **Browse Jobs (Freelancer)**
   - Navigate to /jobs
   - Search and filter by skills
   - View job details
   
3. **Post Job (Client)**
   - Login as client
   - Go to /jobs/post
   - Fill job details and submit
   
4. **Apply for Job (Freelancer)**
   - Find a job
   - Click apply
   - Submit proposal with CV

## Troubleshooting

### Port 8080 Already in Use
```bash
# Change port in application.properties
server.port=8081

# Or via command line
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Maven Not Found
```bash
# Add Maven to PATH or use full path
C:\Apache\maven\bin\mvn --version

# Or reinstall via package manager
winget install Maven.Maven
```

### Database Connection Error
```bash
# Check PostgreSQL is running
pg_isready -h localhost

# Or use H2 instead
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

### Gradle/Build Issues
```bash
# Clean Maven cache
mvn clean

# Rebuild with verbose output
mvn -X clean install

# Skip tests
mvn clean install -DskipTests
```

### Application Won't Start
```bash
# Check logs
tail -f logs/findjob.log

# Run with debug
mvn spring-boot:run -X

# Check Java version
java -version  # Should be 11+
```

## Development Workflow

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Code Style
```bash
# Format code (if formatter configured)
mvn fmt:format

# Check code style
mvn checkstyle:check
```

### Database Migrations
```bash
# Schema is auto-updated by Hibernate (ddl-auto=update)
# For production, use Flyway migrations (future enhancement)
```

### Hot Reload
Spring Boot DevTools is included:
- Any change to Java files triggers restart
- Templates are reloaded immediately
- Static resources refresh automatically

## Production Deployment

### Build Production JAR
```bash
mvn clean package -DskipTests
```

### Deploy to Server
```bash
# Copy JAR to server
scp target/findjob-jobboard-1.0.0.jar user@server:/opt/findjob/

# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://dbserver:5432/findjob

# Run application
java -jar /opt/findjob/findjob-jobboard-1.0.0.jar
```

### Reverse Proxy (Nginx)
```nginx
server {
    listen 80;
    server_name findjob.com;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## Verification Checklist

After setup, verify:
- [ ] Application starts without errors
- [ ] Database connection successful
- [ ] Can access http://localhost:8080
- [ ] Can register new user
- [ ] Can login with credentials
- [ ] Can complete profile
- [ ] Dashboard loads correctly
- [ ] Can view jobs list
