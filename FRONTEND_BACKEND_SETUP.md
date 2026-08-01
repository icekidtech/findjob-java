# FindJob - Frontend & Backend Setup Guide

## Overview

This document outlines the complete setup for the FindJob landing page and authentication system, including both frontend templates and backend controllers.

---

## What's Been Built

### Frontend Components

#### 1. **Landing Page** (`templates/index.html`)
- Hero section with call-to-action buttons
- Feature showcase (6 features highlighting FindJob's advantages)
- "How It Works" section (for freelancers and clients)
- Reputation tier system display
- Final CTA section
- Responsive footer

**Key Features:**
- Mobile-responsive design
- Modern gradient styling
- Bootstrap 5 framework
- Intuitive navigation

#### 2. **Login Page** (`templates/auth/login.html`)
- Email and password input fields
- Remember me checkbox
- Forgot password link
- Social login options (UI only, backend integration needed)
- Form validation with client-side JavaScript
- Error message display
- Link to registration

**Security Features:**
- CSRF token support
- Password field protection
- Form validation

#### 3. **Registration Page** (`templates/auth/register.html`)
- Role selection (Freelancer/Client)
- First/Last name fields
- Email input with validation
- Password with strength indicator
- Confirm password field
- Terms & conditions checkbox
- Client-side form validation
- Password strength meter

**Smart Features:**
- Real-time password strength indicator
- Role-based selection with visual cards
- Form validation before submission
- Terms agreement checkbox

#### 4. **Base Layout** (`templates/layout/base.html`)
- Reusable template structure
- Navigation bar with authentication-aware menu
- Footer with links
- Support for child templates
- Bootstrap integration
- Mobile-responsive navbar

### Styling

#### CSS File (`static/css/style.css`)
- Modern gradient color scheme (#667eea, #764ba2)
- Responsive design breakpoints
- Component styling (cards, buttons, forms)
- Animation definitions
- Utility classes
- Dark theme scrollbar styling
- Loading states and spinners

**Color Scheme:**
- Primary: #667eea to #764ba2 (purple gradient)
- Light: #f8f9fa
- Dark: #1a1a1a
- Accent: #333 (dark text)

### JavaScript

#### Main JS File (`static/js/main.js`)
- Bootstrap tooltip/popover initialization
- Form validation
- Smooth scrolling
- AJAX request handler
- Toast notifications
- Utility functions:
  - Currency formatting
  - Date formatting
  - Debounce/Throttle
  - Viewport detection
  - CSRF token retrieval

---

## Backend Components

### Controllers

#### 1. **HomeController**
```
@Controller
public class HomeController {
    @GetMapping("/") // Landing page
    @GetMapping("/about") // About page
    @GetMapping("/faq") // FAQ page
    @GetMapping("/contact") // Contact page
}
```

#### 2. **AuthController**
```
@Controller
@RequestMapping("/auth")
public class AuthController {
    // Login endpoints
    @GetMapping("/login")
    @PostMapping("/login")
    
    // Registration endpoints
    @GetMapping("/register")
    @PostMapping("/register")
    
    // Logout
    @PostMapping("/logout")
    
    // Password reset (placeholder)
    @GetMapping("/forgot-password")
    @PostMapping("/forgot-password")
}
```

### DTOs (Data Transfer Objects)

#### 1. **LoginRequest**
```java
@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    private Boolean rememberMe;
}
```

#### 2. **RegisterRequest**
```java
@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    @NotBlank
    private String confirmPassword;
    
    @NotBlank
    private String role; // FREELANCER or CLIENT
    
    private Boolean terms;
}
```

### Configuration

#### Application Properties
File: `src/main/resources/application.properties`

**Key Configurations:**
- Server port: 8080
- Database: PostgreSQL
- Thymeleaf: HTML mode with UTF-8 encoding
- JWT: Secret key and expiration settings
- File uploads: 10MB max
- Logging: INFO level for root, DEBUG for app

---

## Setup Instructions

### Prerequisites
- Java 11+ installed
- Maven 3.6+
- PostgreSQL 14+ or MySQL 8.0+
- Git

### Step 1: Database Setup

**For PostgreSQL:**
```sql
CREATE DATABASE findjob;
CREATE USER findjob_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE findjob TO findjob_user;
```

**For MySQL:**
```sql
CREATE DATABASE findjob CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'findjob_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON findjob.* TO 'findjob_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 2: Update Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database (PostgreSQL example)
spring.datasource.url=jdbc:postgresql://localhost:5432/findjob
spring.datasource.username=findjob_user
spring.datasource.password=your_password

# JWT Secret (change in production)
jwt.secret=your_super_secret_jwt_key_change_in_production
```

### Step 3: Build the Project

```bash
# Clean and build
mvn clean build

# Or with tests
mvn clean install
```

### Step 4: Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR
java -jar target/findjob-1.0.0.jar
```

### Step 5: Access the Application

- **Landing Page**: http://localhost:8080/
- **Login**: http://localhost:8080/auth/login
- **Register**: http://localhost:8080/auth/register

---

## File Structure

```
findjob/
├── src/main/
│   ├── java/com/findjob/jobboard/
│   │   └── controller/
│   │       ├── HomeController.java
│   │       └── AuthController.java
│   │   └── dto/
│   │       ├── LoginRequest.java
│   │       └── RegisterRequest.java
│   │
│   └── resources/
│       ├── application.properties
│       ├── templates/
│       │   ├── index.html (landing page)
│       │   ├── layout/
│       │   │   └── base.html
│       │   └── auth/
│       │       ├── login.html
│       │       └── register.html
│       │
│       └── static/
│           ├── css/
│           │   └── style.css
│           └── js/
│               └── main.js
```

---

## Features Implemented

### Frontend Features
- ✅ Responsive landing page with hero section
- ✅ Login form with validation
- ✅ Registration form with role selection
- ✅ Password strength indicator
- ✅ Client-side form validation
- ✅ Mobile-responsive design
- ✅ Modern UI with gradient styling
- ✅ Bootstrap 5 integration
- ✅ Font Awesome icons

### Backend Features
- ✅ HomeController with route mapping
- ✅ AuthController with login/register logic
- ✅ User registration with validation
- ✅ Password encoding (BCrypt)
- ✅ Email uniqueness check
- ✅ Role-based user creation
- ✅ JWT preparation (DTOs ready)
- ✅ Form validation with annotations

### Security Features
- ✅ CSRF token support in forms
- ✅ Password hashing with BCrypt
- ✅ Input validation
- ✅ Role-based enums
- ✅ User status flags (active, verified)

---

## Next Steps

### Phase 2: Core User Management
1. Create User entity with JPA annotations
2. Implement UserRepository
3. Implement UserService
4. Complete profile setup page
5. User profile view/edit pages

### Phase 3: Job Management
1. Create Job entity
2. Implement JobRepository and JobService
3. Create job posting form
4. Implement job listing and filtering
5. Job detail page

### Phase 4: Skill System
1. Create Skill entity
2. Implement skill endorsement logic
3. Skill management interface
4. Endorsement display on profiles

### Phase 5: Additional Features
1. Messaging system
2. Review and ratings
3. Portfolio management
4. Dashboard for users
5. Admin panel

---

## Troubleshooting

### Issue: "Database connection refused"
**Solution:**
- Ensure PostgreSQL/MySQL is running
- Check database URL and credentials in `application.properties`
- Verify database exists and user has privileges

### Issue: "Port 8080 already in use"
**Solution:**
```bash
# Change port in application.properties
server.port=8081

# Or kill process on port 8080
# Windows: netstat -ano | findstr :8080 then taskkill /PID <PID>
# Linux: lsof -i :8080 then kill <PID>
```

### Issue: "Templates not found"
**Solution:**
- Ensure templates are in `src/main/resources/templates/`
- Check Thymeleaf configuration in `application.properties`
- Verify template file names match exactly

### Issue: "Static resources not loading"
**Solution:**
- Ensure CSS/JS are in `src/main/resources/static/`
- Check paths in HTML templates use `th:href="@{/css/...}"`
- Verify `spring.mvc.static-path-pattern` in properties

---

## Performance Tips

1. **Enable Response Compression:**
```properties
server.compression.enabled=true
server.compression.min-response-size=1024
```

2. **Database Connection Pooling:**
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

3. **Cache Static Resources:**
Add cache headers in application configuration

4. **Lazy Load Images:**
Use `loading="lazy"` attribute on images

---

## Security Recommendations

1. **JWT Secret:** Generate a strong, random secret for production
2. **HTTPS:** Always use HTTPS in production
3. **CORS:** Configure CORS properly if using separate frontend
4. **Rate Limiting:** Implement rate limiting on login/register endpoints
5. **Password Policy:** Enforce strong password requirements
6. **Email Verification:** Implement email verification for registration
7. **2FA:** Consider adding two-factor authentication

---

## Development Checklist

- [ ] Database created and running
- [ ] Spring Boot project initialized
- [ ] Dependencies added to pom.xml
- [ ] application.properties configured
- [ ] Controllers created and working
- [ ] DTOs created with validation
- [ ] Templates in correct locations
- [ ] Static files accessible
- [ ] Application starts without errors
- [ ] Landing page displays correctly
- [ ] Login page is accessible
- [ ] Registration page is accessible
- [ ] Form validation working

---

## Testing

### Manual Testing Checklist

1. **Landing Page**
   - [ ] Page loads without errors
   - [ ] All buttons clickable
   - [ ] Responsive on mobile/tablet/desktop

2. **Login Page**
   - [ ] Form validation working
   - [ ] Error messages display correctly
   - [ ] Link to register page works

3. **Registration Page**
   - [ ] Role selection works
   - [ ] Password strength indicator updates
   - [ ] Form validation prevents submission of invalid data
   - [ ] Terms checkbox required

---

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.0/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Last Updated:** August 2026  
**Status:** Landing Page & Auth System Complete ✅
