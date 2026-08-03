# FindJob - System Documentation & Technical Decisions

## Technology Stack Summary

| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| **Framework** | Spring Boot | 3.5.0 | Modern, mature, great ecosystem |
| **Language** | Java | 25 | Strong typing, performance |
| **Web Server** | Tomcat | Embedded | Included with Spring Boot |
| **Template Engine** | Thymeleaf | Latest | Server-side rendering, clean syntax |
| **Database (Dev)** | H2 | Latest | In-memory, zero configuration |
| **Database (Prod)** | PostgreSQL | 14+ | Robust, ACID-compliant, open-source |
| **ORM** | Hibernate/JPA | Via Spring Boot | Reduces boilerplate, maintainable |
| **Security** | Spring Security | 6 | Industry standard, well-tested |
| **Build Tool** | Maven | 3.6+ | Declarative dependency management |
| **CSS Framework** | Bootstrap | 5 | Responsive, widely supported |
| **Password Hashing** | BCrypt | Built-in | Salted, computationally expensive |
| **File Storage** | Local FS / Cloudinary | Local or HTTP | Simple MVP, scales with Cloudinary |
| **Session Storage** | JDBC | Spring Session | Persistent, distributed-ready |

## Key Architectural Decisions

### 1. MVC Pattern Selection
**Decision:** Use Spring MVC with Thymeleaf templates
**Rationale:**
- Server-side rendering simplifies deployment
- No need for separate frontend framework
- Better SEO for job listings
- Simpler for small team development

**Alternatives Considered:**
- REST API + React/Vue → Complexity not justified for MVP
- Microservices → Overkill for current scale

### 2. Database Choice
**Decision:** PostgreSQL for production, H2 for development
**Rationale:**
- PostgreSQL is enterprise-grade, open-source
- H2 allows rapid local development without setup
- Hibernate abstracts SQL differences
- Can switch databases easily if needed

**Trade-offs:**
- Some database-specific features unavailable
- Schema migrations required for production

### 3. Authentication Approach
**Decision:** Session-based authentication with Spring Security
**Rationale:**
- Simpler than JWT for traditional web apps
- JDBC session storage is distributed-ready
- Built-in CSRF protection
- Better for server-side rendering

**Future Option:** JWT for mobile app/REST API

### 4. File Storage Strategy
**Decision:** Local file system with Cloudinary option
**Rationale:**
- Simple implementation for MVP
- No external dependencies initially
- Can upgrade to Cloudinary without code changes
- Cost-effective for small user base

**Scaling Plan:**
- Monitor upload volume
- Switch to Cloudinary when storage > 100GB

### 5. Caching Architecture
**Decision:** Simple in-memory cache with Spring Cache
**Rationale:**
- Minimal configuration
- Good enough for single-instance deployment
- Easy to upgrade to Redis later
- No external dependencies

### 6. Search Implementation
**Decision:** Database query-based search with LIKE
**Rationale:**
- Works well for moderate data volume
- No external search service needed
- Can upgrade to Elasticsearch later
- Simpler development initially

**Indexing Strategy:**
- Indexes on job title, category, client_id
- Ensures < 500ms response time for searches

## Configuration Management

### Environment Profiles
**Development (`h2`):** H2 database, DEBUG logging
**Production (`prod`):** PostgreSQL, INFO logging, security hardening

### Application Properties
```properties
# Database
spring.datasource.url (environment-specific)
spring.jpa.hibernate.ddl-auto=update

# Security
jwt.secret (for future JWT implementation)
spring.session.timeout=30d

# File Upload
spring.servlet.multipart.max-file-size=10MB

# Logging
logging.file.name=logs/findjob.log
```

### Environment Variables (via .env)
- DATABASE_URL
- DATABASE_USERNAME
- DATABASE_PASSWORD
- JWT_SECRET
- CLOUDINARY_API_KEY (optional)

## Monitoring & Observability

### Logging
**Framework:** SLF4J + Logback
**Log Levels:**
- DEBUG: Application logic in com.findjob package
- INFO: Framework and standard events
- ERROR: Exceptions and failures

**Log Output:**
- Console (development)
- File (production, with rotation)

### Metrics Monitored
- Response time per endpoint
- Database query execution time
- File upload size and frequency
- Failed authentication attempts
- Error rates and types
- Server resource usage

### Health Checks
- Database connectivity
- File storage accessibility
- Application startup verification

## Integration Points

### Internal Integrations
- **Spring Security** ↔ **UserRepository:** Authentication lookup
- **JobController** ↔ **JobService** ↔ **JobRepository:** Job operations
- **FileStorageService** ↔ **Local File System:** File I/O
- **ReviewService** ↔ **UserService:** Reputation updates

### External Integrations (Optional)
- **Cloudinary:** Cloud file storage
- **Gmail SMTP:** Email notifications
- **Database Server:** PostgreSQL (production)

## Performance Considerations

### Query Optimization
```sql
-- Jobs by client (indexed on client_id)
SELECT * FROM jobs WHERE client_id = ? ORDER BY posted_at DESC

-- Search jobs (indexed on title)
SELECT * FROM jobs WHERE title LIKE ? ORDER BY relevance

-- User endorsements (indexed on user_id)
SELECT * FROM endorsements WHERE endorsed_user_id = ?
```

### N+1 Query Prevention
- Use JPA EntityGraph for eager loading relationships
- Fetch only necessary columns
- Batch database operations where possible

### Database Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

## Backup & Recovery

### Backup Strategy
**Frequency:** Daily at 02:00 UTC
**Retention:** 30 days rolling window
**Location:** /backups/db/
**Method:** PostgreSQL pg_dump

### Disaster Recovery
| Scenario | RTO | RPO | Action |
|----------|-----|-----|--------|
| DB corruption | 4h | 24h | Restore from backup |
| File loss | 2h | 1h | Restore from backup |
| Server failure | 1h | 30min | Failover to standby |

## Security Hardening

### Spring Security Configuration
```java
// CSRF protection on all POST/PUT/DELETE
http.csrf().csrfTokenRepository(...)

// Session management
http.sessionManagement().maximumSessions(1)

// Password encoding
PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12)
```

### API Security
- All endpoints require authentication (except /auth/**, public pages)
- HTTPS enforced in production
- Rate limiting on login attempts
- Input validation on all forms

### Data Security
- Passwords never stored in logs
- Sensitive data (CV files) in secure directory
- Session data in JDBC (not memory)
- Parameterized queries (JPA prevents SQL injection)

## API Standards

### Request Format
- Content-Type: application/x-www-form-urlencoded (forms)
- Content-Type: multipart/form-data (file uploads)

### Response Format
- HTML (Thymeleaf-rendered)
- Redirect for successful state changes (POST-Redirect-GET pattern)
- Flash messages for user feedback

### Error Handling
- 404 for not found resources
- 401 for unauthenticated requests
- 403 for unauthorized requests
- 500 for server errors
- Descriptive error messages in templates

## Future Enhancement Roadmap

### Phase 1 (Current)
✅ Basic job marketplace
✅ User profiles
✅ Application system

### Phase 2 (Next)
⬜ Skill endorsements
⬜ Reputation tiers
⬜ Advanced search (Elasticsearch)
⬜ Real-time notifications (WebSocket)

### Phase 3 (Later)
⬜ Payment integration (Stripe)
⬜ Escrow system
⬜ Time tracking
⬜ Mobile app

### Phase 4 (Future)
⬜ Microservices architecture
⬜ GraphQL API
⬜ Machine learning recommendations
⬜ Video interview integration
