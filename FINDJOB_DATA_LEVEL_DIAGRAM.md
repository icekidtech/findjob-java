# FindJob - Data Level Diagram (DLD)
## Entity-Relationship Model for Job Board Platform

**Format:** Markdown for draw.io import  
**Database:** MySQL/PostgreSQL  
**Project:** FindJob  

---

## 1. Entity Definitions & Relationships

### ENTITY: User
**Purpose:** Stores user account information (both Freelancers and Clients)

```
┌─────────────────────────────────────┐
│           USER                      │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  skillId           (INT)         │
│     email             (VARCHAR)     │
│     password_hash     (VARCHAR)     │
│     first_name        (VARCHAR)     │
│     last_name         (VARCHAR)     │
│     profile_picture   (VARCHAR)     │
│     headline          (VARCHAR)     │
│     bio               (TEXT)        │
│     location          (VARCHAR)     │
│     phone             (VARCHAR)     │
│     user_role         (ENUM)        │
│     hourly_rate       (DECIMAL)     │
│     reputation_score  (DECIMAL)     │
│     tier_level        (ENUM)        │
│     total_projects    (INT)         │
│     is_verified       (BOOLEAN)     │
│     is_active         (BOOLEAN)     │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Attributes:
  - user_role: ENUM ('FREELANCER', 'CLIENT', 'ADMIN')
  - tier_level: ENUM ('BEGINNER', 'INTERMEDIATE', 'EXPERT', 'TOP_RATED')
```

---

### ENTITY: Skill
**Purpose:** Stores available skills that can be endorsed or required for jobs

```
┌─────────────────────────────────────┐
│           SKILL                     │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│     name              (VARCHAR)     │
│     category          (VARCHAR)     │
│     description       (TEXT)        │
│     is_verified       (BOOLEAN)     │
│     endorsement_count (INT)         │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Attributes:
  - category: (e.g., 'Backend', 'Frontend', 'Mobile', 'Design', etc.)
```

---

### ENTITY: User_Skill (Junction Table)
**Purpose:** Links Users to their Skills (many-to-many relationship)

```
┌─────────────────────────────────────┐
│        USER_SKILL                   │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  user_id           (INT)         │
│ FK  skill_id          (INT)         │
│     proficiency_level (ENUM)        │
│     added_at          (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - user_id → User(id)
  - skill_id → Skill(id)
Attributes:
  - proficiency_level: ENUM ('BEGINNER', 'INTERMEDIATE', 'EXPERT')
```

---

### ENTITY: Job
**Purpose:** Stores job postings created by clients

```
┌─────────────────────────────────────┐
│            JOB                      │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  client_id         (INT)         │
│     title             (VARCHAR)     │
│     description       (TEXT)        │
│     category          (VARCHAR)     │
│     budget_type       (ENUM)        │
│     budget_min        (DECIMAL)     │
│     budget_max        (DECIMAL)     │
│     experience_level  (ENUM)        │
│     job_status        (ENUM)        │
│     duration          (VARCHAR)     │
│     deadline          (DATETIME)    │
│     attachment_url    (VARCHAR)     │
│     views_count       (INT)         │
│     applications_count(INT)         │
│     posted_at         (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
│     completed_at      (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - client_id → User(id) [One Client posts many Jobs]
Attributes:
  - budget_type: ENUM ('FIXED', 'HOURLY')
  - experience_level: ENUM ('ENTRY_LEVEL', 'INTERMEDIATE', 'EXPERT')
  - job_status: ENUM ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')
  - duration: (e.g., '1-3 months', '3-6 months', 'Long term')
```

---

### ENTITY: Job_Skill (Junction Table)
**Purpose:** Links Jobs to required Skills (many-to-many relationship)

```
┌─────────────────────────────────────┐
│        JOB_SKILL                    │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  job_id            (INT)         │
│ FK  skill_id          (INT)         │
│     required_level    (ENUM)        │
│     added_at          (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - job_id → Job(id)
  - skill_id → Skill(id)
Attributes:
  - required_level: ENUM ('BEGINNER', 'INTERMEDIATE', 'EXPERT')
```

---

### ENTITY: Job_Application
**Purpose:** Stores freelancer applications/bids for jobs

```
┌─────────────────────────────────────┐
│     JOB_APPLICATION                 │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  job_id            (INT)         │
│ FK  freelancer_id     (INT)         │
│     proposed_budget   (DECIMAL)     │
│     proposal_text     (TEXT)        │
│     attachment_url    (VARCHAR)     │
│     application_status(ENUM)        │
│     cover_letter      (TEXT)        │
│     applied_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - job_id → Job(id) [One Job has many Applications]
  - freelancer_id → User(id) [One Freelancer submits many Applications]
Attributes:
  - application_status: ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')
```

---

### ENTITY: Contract (Optional but Important)
**Purpose:** Represents an accepted job application (active contract)

```
┌─────────────────────────────────────┐
│         CONTRACT                    │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  job_id            (INT)         │
│ FK  freelancer_id     (INT)         │
│ FK  client_id         (INT)         │
│     contract_status   (ENUM)        │
│     start_date        (DATE)        │
│     end_date          (DATE)        │
│     amount            (DECIMAL)     │
│     milestone_count   (INT)         │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
│     completed_at      (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - job_id → Job(id)
  - freelancer_id → User(id)
  - client_id → User(id)
Attributes:
  - contract_status: ENUM ('ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED', 'DISPUTED')
```

---

### ENTITY: Review
**Purpose:** Stores ratings and reviews for completed jobs

```
┌─────────────────────────────────────┐
│          REVIEW                     │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  job_id            (INT)         │
│ FK  contract_id       (INT)         │
│ FK  author_id         (INT)         │
│ FK  recipient_id      (INT)         │
│     rating            (INT)         │
│     review_text       (TEXT)        │
│     rating_quality    (INT)         │
│     rating_communication(INT)       │
│     rating_professionalism(INT)     │
│     is_verified       (BOOLEAN)     │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - job_id → Job(id)
  - contract_id → Contract(id)
  - author_id → User(id) [Reviewer]
  - recipient_id → User(id) [Reviewed]
Attributes:
  - rating: (1-5 stars)
  - rating_quality, rating_communication, rating_professionalism: (1-5 each)
```

---

### ENTITY: Endorsement
**Purpose:** Tracks skill endorsements between freelancers

```
┌─────────────────────────────────────┐
│       ENDORSEMENT                   │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  endorser_id       (INT)         │
│ FK  endorsed_user_id  (INT)         │
│ FK  skill_id          (INT)         │
│     endorsement_type  (ENUM)        │
│     is_approved       (BOOLEAN)     │
│     expires_at        (DATETIME)    │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - endorser_id → User(id) [Who endorsed]
  - endorsed_user_id → User(id) [Who was endorsed]
  - skill_id → Skill(id)
Attributes:
  - endorsement_type: ENUM ('PEER', 'CLIENT', 'VERIFIED')
```

---

### ENTITY: Message
**Purpose:** Stores direct messages between freelancers and clients

```
┌─────────────────────────────────────┐
│         MESSAGE                     │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  sender_id         (INT)         │
│ FK  recipient_id      (INT)         │
│ FK  job_id            (INT)         │
│ FK  application_id    (INT)         │
│     message_text      (TEXT)        │
│     attachment_url    (VARCHAR)     │
│     is_read           (BOOLEAN)     │
│     read_at           (DATETIME)    │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - sender_id → User(id)
  - recipient_id → User(id)
  - job_id → Job(id) [Optional, for job context]
  - application_id → Job_Application(id) [Optional, for app context]
```

---

### ENTITY: Portfolio_Item
**Purpose:** Stores freelancer portfolio/work samples

```
┌─────────────────────────────────────┐
│      PORTFOLIO_ITEM                 │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  freelancer_id     (INT)         │
│ FK  job_id            (INT)         │
│     title             (VARCHAR)     │
│     description       (TEXT)        │
│     technologies      (VARCHAR)     │
│     image_url         (VARCHAR)     │
│     external_link     (VARCHAR)     │
│     completion_date   (DATE)        │
│     views_count       (INT)         │
│     is_public         (BOOLEAN)     │
│     created_at        (TIMESTAMP)   │
│     updated_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - freelancer_id → User(id)
  - job_id → Job(id) [Optional, links to original job]
```

---

### ENTITY: Notification (Optional)
**Purpose:** Stores user notifications

```
┌─────────────────────────────────────┐
│      NOTIFICATION                   │
├─────────────────────────────────────┤
│ PK  id                (INT)         │
│ FK  user_id           (INT)         │
│     notification_type (ENUM)        │
│     title             (VARCHAR)     │
│     message           (TEXT)        │
│ FK  related_job_id    (INT)         │
│ FK  related_user_id   (INT)         │
│     is_read           (BOOLEAN)     │
│     read_at           (DATETIME)    │
│     created_at        (TIMESTAMP)   │
└─────────────────────────────────────┘
Relationships:
  - user_id → User(id)
  - related_job_id → Job(id) [Optional]
  - related_user_id → User(id) [Optional]
Attributes:
  - notification_type: ENUM ('NEW_APPLICATION', 'APPLICATION_ACCEPTED', 
                             'JOB_COMPLETED', 'NEW_MESSAGE', 'ENDORSEMENT',
                             'REVIEW_RECEIVED', 'JOB_POSTED')
```

---

## 2. Relationship Summary

| From Entity | To Entity | Type | Cardinality | Notes |
|-------------|-----------|------|-------------|-------|
| User | Skill | M:M | Via User_Skill | Freelancer can have multiple skills |
| Job | Skill | M:M | Via Job_Skill | Job requires multiple skills |
| User | Job | 1:M | - | Client posts many jobs |
| Job | Job_Application | 1:M | - | Job receives many applications |
| User | Job_Application | 1:M | - | Freelancer applies to many jobs |
| Job_Application | Contract | 1:1 | - | Accepted application becomes contract |
| Job | Review | 1:M | - | Multiple reviews per job |
| User | Review | 1:M | - | User receives multiple reviews |
| User | Endorsement | M:M | - | Users endorse each other's skills |
| User | Message | 1:M | - | Users send and receive messages |
| User | Portfolio_Item | 1:M | - | Freelancer has multiple portfolio items |
| User | Notification | 1:M | - | User receives multiple notifications |

---

## 3. ER Diagram (Text-based for draw.io reference)

```
                    ┌──────────────┐
                    │     USER     │
                    ├──────────────┤
                    │ id (PK)      │
                    │ email        │
                    │ password_hash│
                    │ name         │
                    │ bio          │
                    │ user_role    │
                    │ tier_level   │
                    └──────────────┘
                    /      |      \
                   /       |       \
                  /        |        \
                 /         |         \
    ┌─────────────────┐ ┌──────────────────┐ ┌──────────────────┐
    │  USER_SKILL(M2M)│ │ JOB              │ │ ENDORSEMENT      │
    ├─────────────────┤ ├──────────────────┤ ├──────────────────┤
    │ user_id (FK)    │ │ id (PK)          │ │ endorser_id (FK) │
    │ skill_id (FK)   │ │ client_id (FK)   │ │ endorsed_id (FK) │
    │ proficiency     │ │ title            │ │ skill_id (FK)    │
    └─────────────────┘ │ description      │ │ created_at       │
           ↓            │ budget           │ └──────────────────┘
    ┌──────────────┐    │ job_status       │
    │    SKILL     │    │ posted_at        │
    ├──────────────┤    └──────────────────┘
    │ id (PK)      │           │
    │ name         │           │
    │ category     │           ├─ 1:M ─→ ┌──────────────────────┐
    │ description  │           │         │ JOB_APPLICATION      │
    └──────────────┘           │         ├──────────────────────┤
            ↑                   │         │ id (PK)              │
            │                   │         │ job_id (FK)          │
      M:M via                   │         │ freelancer_id (FK)   │
    JOB_SKILL                   │         │ proposed_budget      │
            │                   │         │ application_status   │
    ┌───────┴───────┐           │         └──────────────────────┘
    │               │           │                  │
    │      JOB_SKILL│           │                  ├─ 1:1 ─→ ┌──────────────────┐
    │      (M:M)    │           │                  │         │ CONTRACT         │
    ├───────────────┤           │                  │         ├──────────────────┤
    │ job_id (FK)   │           │                  │         │ id (PK)          │
    │ skill_id (FK) │           │                  │         │ freelancer_id(FK)│
    │ required_level│           │                  │         │ job_id (FK)      │
    └───────────────┘           │                  │         │ amount           │
                                │                  │         └──────────────────┘
                                │                  │              │
                                │                  └── 1:M ──→ ┌──────────────┐
                                │                             │   REVIEW     │
                                │                             ├──────────────┤
                                │                             │ id (PK)      │
                                │                             │ author_id(FK)│
                                │                             │ recipient_id │
                                │                             │ rating       │
                                │                             │ review_text  │
                                │                             └──────────────┘
                                │
                                └── 1:M ──→ ┌──────────────────────┐
                                           │ MESSAGE              │
                                           ├──────────────────────┤
                                           │ id (PK)              │
                                           │ sender_id (FK)       │
                                           │ recipient_id (FK)    │
                                           │ message_text         │
                                           │ is_read              │
                                           └──────────────────────┘

                    ┌──────────────────────┐
                    │ PORTFOLIO_ITEM       │
                    ├──────────────────────┤
                    │ id (PK)              │
                    │ freelancer_id (FK)   │
                    │ title                │
                    │ description          │
                    │ image_url            │
                    │ technologies         │
                    └──────────────────────┘
                           ↑
                           │
                        1:M │
                           │
                      USER(id)


                    ┌──────────────────────┐
                    │ NOTIFICATION         │
                    ├──────────────────────┤
                    │ id (PK)              │
                    │ user_id (FK)         │
                    │ notification_type    │
                    │ message              │
                    │ is_read              │
                    │ created_at           │
                    └──────────────────────┘
                           ↑
                           │
                        1:M │
                           │
                      USER(id)
```

---

## 4. Key Relationships Explained

### User → Job (1:M)
- One user (CLIENT) can post many jobs
- Relationship: `User.id = Job.client_id`

### Job → Job_Application (1:M)
- One job receives many applications from different freelancers
- Relationship: `Job.id = Job_Application.job_id`

### User → Job_Application (1:M)
- One freelancer (user) can apply to many jobs
- Relationship: `User.id = Job_Application.freelancer_id`

### User ↔ Skill (M:M)
- One user can have multiple skills
- One skill can be associated with multiple users
- Junction table: `User_Skill`
- Relationships: `User_Skill.user_id = User.id` AND `User_Skill.skill_id = Skill.id`

### Job ↔ Skill (M:M)
- One job requires multiple skills
- One skill can be required by multiple jobs
- Junction table: `Job_Skill`
- Relationships: `Job_Skill.job_id = Job.id` AND `Job_Skill.skill_id = Skill.id`

### User ↔ User (M:M via Endorsement)
- Freelancers can endorse each other for skills
- Endorser endorses Endorsed User for a Skill
- Junction table: `Endorsement`
- Relationships: `Endorsement.endorser_id = User.id` AND `Endorsement.endorsed_user_id = User.id`

### Job_Application → Contract (1:1)
- When a freelancer is selected, application becomes contract
- Relationship: `Job_Application.id → Contract.application_id` (optional)

### Contract → Review (1:M)
- After contract completion, both parties can leave reviews
- Relationship: `Contract.id = Review.contract_id`

### User → Message (1:M)
- One user sends/receives many messages
- Relationships: `Message.sender_id = User.id` OR `Message.recipient_id = User.id`

---

## 5. Database Indexes (Performance Optimization)

```sql
-- User table indexes
CREATE INDEX idx_user_email ON User(email);
CREATE INDEX idx_user_role ON User(user_role);
CREATE INDEX idx_user_tier ON User(tier_level);
CREATE INDEX idx_user_created ON User(created_at);

-- Job table indexes
CREATE INDEX idx_job_client ON Job(client_id);
CREATE INDEX idx_job_status ON Job(job_status);
CREATE INDEX idx_job_category ON Job(category);
CREATE INDEX idx_job_posted ON Job(posted_at);
CREATE INDEX idx_job_deadline ON Job(deadline);

-- Job_Application indexes
CREATE INDEX idx_app_job ON Job_Application(job_id);
CREATE INDEX idx_app_freelancer ON Job_Application(freelancer_id);
CREATE INDEX idx_app_status ON Job_Application(application_status);

-- Contract indexes
CREATE INDEX idx_contract_job ON Contract(job_id);
CREATE INDEX idx_contract_freelancer ON Contract(freelancer_id);
CREATE INDEX idx_contract_status ON Contract(contract_status);

-- Review indexes
CREATE INDEX idx_review_recipient ON Review(recipient_id);
CREATE INDEX idx_review_job ON Review(job_id);

-- Message indexes
CREATE INDEX idx_message_recipient ON Message(recipient_id);
CREATE INDEX idx_message_sender ON Message(sender_id);
CREATE INDEX idx_message_created ON Message(created_at);

-- Endorsement indexes
CREATE INDEX idx_endorsement_user ON Endorsement(endorsed_user_id);
CREATE INDEX idx_endorsement_skill ON Endorsement(skill_id);

-- Portfolio indexes
CREATE INDEX idx_portfolio_freelancer ON Portfolio_Item(freelancer_id);
```

---

## 6. Import Instructions for draw.io

1. **Create Entities:**
   - Copy each entity box above
   - Create rectangles in draw.io with the same structure
   - Add attributes as text within boxes

2. **Create Relationships:**
   - Use arrows to connect entities
   - Label arrows with relationship type (1:M, M:M, 1:1)
   - Use filled circles for primary keys (PK)
   - Use hollow circles for foreign keys (FK)

3. **Color Coding (Optional):**
   - **Core Entities:** Blue (User, Job, Skill)
   - **Junction Tables:** Yellow (User_Skill, Job_Skill, Endorsement)
   - **Transaction Entities:** Green (Job_Application, Contract)
   - **Support Entities:** Gray (Message, Review, Portfolio_Item, Notification)

---

## 7. SQL Schema Generation Example

```sql
CREATE TABLE User (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  profile_picture VARCHAR(500),
  headline VARCHAR(200),
  bio TEXT,
  location VARCHAR(100),
  phone VARCHAR(20),
  user_role ENUM('FREELANCER', 'CLIENT', 'ADMIN') NOT NULL,
  hourly_rate DECIMAL(10, 2),
  reputation_score DECIMAL(3, 2) DEFAULT 0,
  tier_level ENUM('BEGINNER', 'INTERMEDIATE', 'EXPERT', 'TOP_RATED') DEFAULT 'BEGINNER',
  total_projects INT DEFAULT 0,
  is_verified BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_email (email),
  INDEX idx_user_role (user_role),
  INDEX idx_user_tier (tier_level)
);

-- Continue with other tables following similar structure...
```

---

**End of DLD Documentation**

---

### Notes for Implementation:
- All timestamps should use UTC
- Foreign key constraints should be enabled
- Use appropriate character sets (UTF-8)
- Consider database backup strategy
- Plan for data archival (old completed jobs, messages)
- Monitor query performance after implementing indexes
