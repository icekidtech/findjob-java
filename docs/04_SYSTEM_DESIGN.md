# FindJob - System Design & Architecture

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                     │
│         (Thymeleaf Templates + Bootstrap 5)             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Application                    │
├─────────────────────────────────────────────────────────┤
│  Controllers  │  Services  │  Repositories  │  Models   │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                Data Access Layer (JPA)                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          H2 (Dev) / PostgreSQL (Prod) Database          │
└─────────────────────────────────────────────────────────┘
```

## Layered Architecture

### 1. Presentation Layer
- **Thymeleaf Template Engine** for server-side rendering
- **Bootstrap 5** for responsive CSS framework
- **Form Validation** with client and server-side checks
- **Static Resources** (CSS, JS, images)

**Key Templates:**
- `auth/login.html` - Authentication pages
- `jobs/list.html` - Job browsing
- `jobs/detail.html` - Job details
- `profile/view.html` - User profiles
- `dashboard/freelancer.html` - Freelancer dashboard
- `dashboard/client.html` - Client dashboard

### 2. Controller Layer
- **AuthController:** Registration, login, logout
- **JobController:** Job CRUD, browsing, filtering
- **ProfileController:** User profile management
- **ApplicationController:** Application review/decision
- **EndorsementController:** Skill endorsements
- **DashboardController:** Role-based dashboards

**Responsibilities:**
- Handle HTTP requests
- Validate input parameters
- Call appropriate services
- Return views or redirect responses
- Manage user authentication context

### 3. Service Layer
**Core Services:**
- **UserService:** User creation, authentication, profile updates
- **JobService:** Job CRUD, search, filtering, pagination
- **JobApplicationService:** Application submission, status updates
- **EndorsementService:** Endorsement creation, validation
- **ReviewService:** Review submission, reputation calculation
- **MessageService:** Direct messaging between users
- **FileStorageService:** Local file handling (CV, portfolios)
- **SkillService:** Skill database management
- **SavedJobService:** Job bookmarking
- **JobViewService:** Track unique views per user

**Responsibilities:**
- Business logic implementation
- Data validation
- Transaction management
- Cross-entity operations
- External service integration

### 4. Repository Layer (Data Access)
**JPA Repositories:**
- UserRepository - User CRUD and queries
- JobRepository - Job CRUD with pagination
- JobApplicationRepository - Application queries
- SkillRepository - Skill management
- EndorsementRepository - Endorsement queries
- ReviewRepository - Review queries
- MessageRepository - Message queries
- SavedJobRepository - Bookmark queries

**Responsibilities:**
- Database CRUD operations
- Custom query methods
- Pagination and sorting
- Transaction boundaries

### 5. Database Layer
**Core Entities:**
- **User** - Freelancers and Clients
- **Job** - Job postings with status
- **JobApplication** - Freelancer applications
- **Skill** - Available skills database
- **Endorsement** - Peer skill verification
- **Review** - Post-project ratings
- **Message** - Direct messaging
- **SavedJob** - Job bookmarks
- **JobView** - View tracking

**Indexes (Performance):**
```sql
idx_user_email              -- Fast email lookup
idx_job_client_id          -- Client's jobs query
idx_job_status             -- Filter by status
idx_app_freelancer_id      -- Freelancer's applications
idx_endorsement_user_id    -- User's endorsements
```

## Data Flow Examples

### User Registration Flow
```
User Input (Register Form)
    ↓
AuthController.register()
    ↓
UserService.save(newUser)
    ↓
UserRepository.save()
    ↓
Database Insert
    ↓
Redirect to Profile Complete
```

### Job Application Flow
```
Freelancer Submit Application
    ↓
JobController.submitApplication()
    ↓
JobApplicationService.submit()
    ↓
JobApplication Repository.save()
    ↓
Job.incrementApplications()
    ↓
Database Update
    ↓
Success Message
```

### Reputation Calculation Flow
```
Project Completed
    ↓
ReviewService.submitReview()
    ↓
ReviewRepository.save()
    ↓
ReviewService.calculateReputation(userId)
    ↓
Get all reviews for user
    ↓
Calculate average rating
    ↓
UserService.updateReputation()
    ↓
Update tier level
    ↓
Database Update
```

## Security Architecture

### Authentication
- **Method:** Spring Security with form-based login
- **Password Hashing:** BCrypt with default strength
- **Session Management:** JDBC-based persistent sessions
- **Session Timeout:** 30 days inactivity

### Authorization
- **Role-Based Access Control:** FREELANCER, CLIENT, ADMIN roles
- **URL-Level Protection:** @PreAuthorize annotations
- **Method-Level Security:** Service layer method checks

### Data Protection
- **CSRF Tokens:** On all state-changing forms
- **SQL Injection:** Prevention via JPA parameterized queries
- **XSS Prevention:** Thymeleaf auto-escaping
- **Password Security:** BCrypt hashing, strong requirements
- **File Upload:** Type and size restrictions

## Performance Optimization

### Caching Strategy
- **Spring Cache:** Simple in-memory cache
- **Cached Data:** Skill list, popular job categories
- **TTL:** 1 hour default

### Database Optimization
- **Connection Pooling:** HikariCP (default)
- **Batch Operations:** Enable batch size 20
- **Lazy Loading:** Use FetchType.LAZY for relationships
- **N+1 Query Prevention:** Proper entity graphs

### Frontend Performance
- **Static Asset Caching:** Browser cache headers
- **Minification:** CSS and JS compression
- **CDN Ready:** Static resources separated for CDN
- **Pagination:** Max 12-20 items per page

## Scalability Considerations

### Stateless Design
- JWT-ready architecture (no session state in code)
- User context from authentication
- Database as source of truth

### Load Balancing Ready
- Stateless session handling (JDBC)
- No file locks or local state
- Horizontally scalable

### Future Enhancements
- Cache layer (Redis) for sessions
- Read replicas for reporting queries
- Message queue for async operations
- Microservices separation if needed

## Technical Decisions

| Decision | Reasoning |
|----------|-----------|
| Spring Boot | Mature, battle-tested framework |
| Thymeleaf | Server-side rendering, simple templates |
| JPA/Hibernate | ORM reduces boilerplate, better maintainability |
| H2 for Dev | Zero-config testing database |
| PostgreSQL for Prod | Robust, scalable relational database |
| BCrypt Passwords | Industry standard hashing algorithm |
| Local File Storage | Simplicity for MVP, Cloudinary as upgrade |

## Deployment Architecture

### Development
- Single H2 database
- Spring Boot embedded Tomcat
- Hot reload with DevTools

### Production
- PostgreSQL on separate server
- Application on application server
- Reverse proxy (Nginx) for SSL/static files
- Daily automated backups
- Log aggregation

## Future Enhancement Areas

1. **Caching Layer** - Redis for sessions and frequent queries
2. **Asynchronous Processing** - Message queue for emails, notifications
3. **Search Engine** - Elasticsearch for full-text search
4. **Real-time Features** - WebSocket for live notifications
5. **Microservices** - Split into separate services if needed
6. **API Gateway** - For mobile app or third-party integrations
