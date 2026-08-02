# FindJob Project Setup Guide

Quick start guide for setting up and running the FindJob application.

## Prerequisites Check

### 1. Java Installation
```bash
java --version
# Should show Java 11 or higher (you have Java 26.0.1 ✓)
```

### 2. Maven Installation

**Status**: Maven 3.9.16 installed ✓

Verify installation:
```bash
mvn --version
```

## Database Setup (Choose One)

### Quick Start: H2 (In-Memory - No Setup Required!)

Best for initial development and testing. **Recommended for first-time run.**

**Using PowerShell:**
```powershell
.\build.ps1 -Action run-h2
```

**Using Batch:**
```batch
build.bat
# Then select option 4
```

**Using Maven directly:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Access H2 Console: http://localhost:8080/h2-console

---

### Full Setup: PostgreSQL

**Windows Installation:**
1. Download from https://www.postgresql.org/download/windows/
2. Run installer (default port: 5432)
3. Remember the password for `postgres` user

**After Installation:**
```bash
# Create database
createdb -U postgres findjob

# Or use psql:
psql -U postgres
CREATE DATABASE findjob;
\q
```

**Update Configuration:**
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/findjob
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

---

## Build & Run

### Using Build Scripts (Recommended)

**PowerShell (Recommended for Windows):**
```powershell
# Build without tests (faster)
.\build.ps1 -Action clean-build

# Run with H2 (development)
.\build.ps1 -Action run-h2

# Run with PostgreSQL (production)
.\build.ps1 -Action run
```

**Batch Script:**
```batch
# Run the interactive menu
build.bat
```

### Using Maven Directly

**Development (H2 - Recommended)**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

**Production (PostgreSQL)**
```bash
mvn spring-boot:run
```

**Build only (skip tests)**
```bash
mvn clean install -DskipTests
```

### Step 3: Access Application

Open browser to: **http://localhost:8080**

- Homepage: http://localhost:8080
- Register: http://localhost:8080/auth/register
- Login: http://localhost:8080/auth/login
- H2 Console (dev only): http://localhost:8080/h2-console

---

## Resolving Common Issues

### Maven Cache Error
If you see errors about "maven-surefire-plugin:3.2.0 was not found", the cache is corrupted.

**Solution:**
```bash
# Option 1: Using build script
.\build.ps1 -Action clear-cache

# Option 2: Manual cache clear
rmdir %USERPROFILE%\.m2\repository

# Option 3: Force Maven update
mvn clean install -DskipTests -U
```

### Port 8080 Already in Use
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

### Connection Refused (PostgreSQL)
- Ensure PostgreSQL is running
- Use H2 profile instead: `.\build.ps1 -Action run-h2`

### OutOfMemory during build
```bash
set MAVEN_OPTS=-Xmx1024m
mvn clean install
```

---

## Project Files Created

✅ **pom.xml** - Maven configuration with all dependencies
✅ **FindJobApplication.java** - Spring Boot entry point
✅ **application-h2.properties** - H2 database configuration
✅ **build.ps1** - PowerShell build script
✅ **build.bat** - Batch build script
✅ **README.md** - Complete documentation
✅ **SETUP.md** - This file

---

## Frontend Templates Created (Phase 3 & 4)

### Phase 3 - Job Management
✅ `templates/jobs/list.html` - Browse jobs listing page
✅ `templates/jobs/detail.html` - Job detail page
✅ `templates/jobs/post.html` - Post a new job
✅ `templates/jobs/apply.html` - Submit job application/proposal

### Phase 4 - Endorsements & Reviews
✅ `templates/endorsements/list.html` - View endorsements
✅ `templates/reviews/list.html` - View reviews/ratings
✅ `templates/reviews/submit.html` - Submit a review

---

## What's Next?

After successful build:

1. **Test the Application**
   - Register as a Freelancer/Client
   - Browse jobs
   - Submit proposals
   - View profile
   - Check endorsements & reviews

2. **Frontend Development**
   - Styling refinements
   - JavaScript interactions
   - Form validations

3. **Backend Enhancements**
   - ReviewController implementation
   - Additional service methods
   - API error handling

4. **Database**
   - Data validation
   - Relationships verification
   - Performance optimization

5. **Testing**
   - Unit tests for services
   - Integration tests for controllers
   - UI/UX testing

---

## Project Structure Overview

```
FindJob/
├── pom.xml                          # ✓ Maven configuration
├── build.ps1                        # ✓ PowerShell build script
├── build.bat                        # ✓ Batch build script
├── README.md                        # ✓ Full documentation
├── SETUP.md                         # ✓ This setup guide
│
├── src/main/
│   ├── java/com/findjob/jobboard/
│   │   ├── FindJobApplication.java  # ✓ Entry point
│   │   ├── controller/              # Controllers (7 files)
│   │   ├── service/                 # Services (6 files)
│   │   ├── repository/              # Repositories (6 files)
│   │   ├── model/                   # Models (12 files)
│   │   └── dto/                     # DTOs (6 files)
│   │
│   └── resources/
│       ├── application.properties               # ✓ Main config
│       ├── application-h2.properties            # ✓ H2 config
│       ├── templates/                          # HTML templates
│       │   ├── index.html                      # ✓ Homepage
│       │   ├── auth/                           # ✓ Auth pages (2 files)
│       │   ├── dashboard/                      # ✓ Dashboard pages (2 files)
│       │   ├── profile/                        # ✓ Profile pages (3 files)
│       │   ├── jobs/                           # ✓ Job pages (4 files)
│       │   ├── endorsements/                   # ✓ Endorsement page (1 file)
│       │   ├── reviews/                        # ✓ Review pages (2 files)
│       │   └── layout/                         # ✓ Layout templates (1 file)
│       └── static/                             # CSS, JS, images
│
└── .git/                            # Git repository
```

---

## Phase Progress

| Phase | Feature | Backend | Frontend | Status |
|-------|---------|---------|----------|--------|
| 1 | Auth & Landing | ✅ | ✅ | Complete |
| 2 | User Management | ✅ | ✅ | Complete |
| 3 | Job System | ✅ | ✅ | Complete |
| 4 | Endorsements & Reviews | ✅ | ✅ | Complete |

---

## Useful Commands

```bash
# Build
mvn clean install -DskipTests

# Run tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Generate documentation
mvn site

# Display dependency tree
mvn dependency:tree

# Force update repositories
mvn clean install -U

# Run with specific port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"

# Enable remote debugging
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

---

## Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Review README.md for detailed documentation
3. Ensure Maven and Java versions match requirements
4. Clear Maven cache if you have resolution errors: `.\build.ps1 -Action clear-cache`

**Happy coding! 🚀**

