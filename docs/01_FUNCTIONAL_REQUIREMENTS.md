# FindJob - Functional Requirements

## Overview
FindJob is a hybrid job marketplace and professional network connecting freelancers with clients through skill endorsements, portfolio verification, and transparent rating systems.

## Core Features

### 1. User Management
- Email-based registration (Freelancer/Client roles)
- Secure login with BCrypt password hashing
- Role-based dashboards
- Profile completion after registration
- Account deactivation capability

### 2. Job Management
**Client Features:**
- Post jobs with title, description, budget, skills requirement
- Set experience level (Entry/Intermediate/Expert)
- Define budget type (Fixed/Hourly) and currency
- Track job applications and views
- Accept/reject freelancer proposals
- Close job postings

**Freelancer Features:**
- Browse and search jobs by skills, category, budget
- Filter by experience level and posted date
- View detailed job information and client profile
- Submit applications with cover letter and CV
- Bookmark favorite jobs

### 3. Applications & Bidding
- Freelancers submit proposals with cover letter
- Upload CV file support (local storage)
- Portfolio URL linking
- Client reviews and accepts/rejects applications
- Withdrawal capability for freelancers

### 4. Skill Endorsement System
- Pre-defined skill database
- Peer endorsement capability (freelancer to freelancer)
- Client endorsement after project completion
- Admin verification badges
- Endorsement count tracking on profiles
- Endorsement expiry support

### 5. Ratings & Reviews
- Post-project rating system (1-5 stars)
- Written review/feedback
- Detailed scoring: Quality, Communication, Professionalism
- Reputation score calculation
- Tier system progression (Beginner → Expert → Top Rated)

### 6. Freelancer Tier System
| Tier | Projects | Rating | Benefits |
|------|----------|--------|----------|
| **Beginner** | 0-50 | 3.5+ | Basic profile |
| **Intermediate** | 50-150 | 4.0+ | Enhanced visibility |
| **Expert** | 150+ | 4.2+ | Premium jobs access |
| **Top Rated** | 200+ | 4.5+ | Featured placement |

### 7. Messaging System
- Direct messaging between freelancers and clients
- Application-specific discussion threads
- Message history tracking
- Read status indicators

### 8. Portfolio Management
- Add project samples with images/links
- List technologies used
- Completion dates
- Public visibility toggle
- Integration with freelancer profiles

### 9. Profile Features
- Headline, bio, location
- Profile picture support
- Hourly rate (freelancers)
- Company information (clients)
- Work history and completed projects
- Reputation score display
- Tier badge display

### 10. Search & Discovery
- Full-text search on job titles/descriptions
- Filter by skills, budget range, category
- Filter by experience level and posted date
- Sort by relevance, newest, budget
- Pagination support
- Saved jobs/bookmarks

## Must-Have Features (MVP)
- ✅ User registration and authentication
- ✅ Job posting and browsing
- ✅ Job applications
- ✅ Basic profiles
- ✅ Rating system
- ✅ Messaging
- ✅ Search and filters
- ✅ Dashboards

## Should-Have Features
- [ ] Skill endorsements
- [ ] Tier system
- [ ] Portfolio management
- [ ] Advanced analytics
- [ ] Notifications

## Nice-to-Have Features
- [ ] Payment/escrow system
- [ ] Time tracking
- [ ] Dispute resolution
- [ ] Mobile app
- [ ] API integrations
