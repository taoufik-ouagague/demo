# Comprehensive Security System Documentation

## Overview
This application implements a multi-level organizational security system with JWT Bearer token authentication, role-based access control (RBAC), and permission-based authorization.

## Architecture

### 1. Authentication (JWT Bearer Tokens)
- **JwtUtils**: Generates, validates, and parses JWT tokens
- **JwtAuthenticationFilter**: Intercepts requests to validate Bearer tokens
- Token Expiration: 24 hours (configurable)
- Signing Algorithm: HS256

#### Login Response
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJkYXZpZCIsImlhdCI6MTc3MjExNTUxMywiZXhwIjoxNzcyMjAxOTEzfQ...",
  "type": "Bearer",
  "id": 1,
  "username": "david",
  "email": "david@example.com",
  "roles": []
}
```

### 2. Simplified User Model
- User accounts with authentication support
- Fields: login, email, pwd, id_role, libelle, date_creation, date_desactivation, status
- Role reference stored as id_role (Integer, nullable)
- Status flag for enabling/disabling accounts

**Note:** Organization and role relationships are currently simplified. The id_role field allows for role reference but role management is minimal in the current implementation.

### 3. Roles & Permissions (RBAC System)

#### 3 Core Roles
1. **ROLE_ADMIN**: Full system access (22 droits/permissions)
2. **ROLE_NORMAL_USER**: Regular user with limited permissions (5 droits)
3. **ROLE_CLIENT**: Client with minimal permissions (2 droits)

#### 22 Permissions (Droits)
**User Management (10 droits):**
- USER_ENABLE_DISABLE - Enable/disable user accounts
- USER_PASSWORD_RESET - Reset user passwords
- USER_ASSIGN_ROLE - Assign roles to users
- USER_REMOVE_ROLE - Remove roles from users
- USER_ASSIGN_DROIT - Assign permissions to users
- USER_REMOVE_DROIT - Remove permissions from users
- USER_CREATE - Create users
- USER_READ - View user details
- USER_UPDATE - Update user information
- USER_DELETE - Delete users

**Role Management (5 droits):**
- ROLE_CREATE - Create new roles
- ROLE_READ - View role details
- ROLE_UPDATE - Update role information
- ROLE_DELETE - Delete roles
- ROLE_ASSIGN_DROIT - Assign permissions to roles

**Permission Management (4 droits):**
- DROIT_CREATE - Create new permissions
- DROIT_READ - View permissions
- DROIT_UPDATE - Update permissions
- DROIT_DELETE - Delete permissions

**General (3 droits):**
- ADMIN_ACCESS - Access admin panel
- VIEW_DASHBOARD - View dashboard
- VIEW_REPORTS - View reports

#### Admin Endpoints for RBAC
Complete API management for users, roles, and permissions

**Requires JWT Bearer Token Authentication**

## User Model

```java
User {
  Integer id;
  String login;  // Unique identifier
  String email;
  String pwd;  // BCrypt encrypted
  Integer id_role;  // Role reference (nullable)
  String libelle;  // Description/label
  LocalDateTime date_creation;
  LocalDateTime date_desactivation;
  boolean status;  // Default: true
}
```

## Database Schema

### Current Tables
1. **users** - User accounts:
   - id (Integer, PK)
   - login (String, unique)
   - email (String, unique)
   - pwd (String, BCrypt encrypted)
   - id_role (Integer, FK to roles)
   - libelle (String, nullable)
   - date_creation (LocalDateTime)
   - date_desactivation (LocalDateTime, nullable)
   - status (Boolean, default: true)

2. **roles** - Role definitions:
   - id (Integer, PK)
   - code (String, unique) - e.g., "ROLE_ADMIN", "ROLE_NORMAL_USER", "ROLE_CLIENT"
   - libelle (String) - Display name
   - description (String, nullable)

3. **droit** - Permission definitions (22 permissions):
   - id (Integer, PK)
   - code (String, unique) - e.g., "USER_ENABLE_DISABLE"
   - libelle (String) - Display name
   - description (String, nullable)

4. **role_droit** - Role-Permission associations (Many-to-Many):
   - role_id (Integer, FK to roles)
   - droit_id (Integer, FK to droit)
   - Eagerly loaded with roles for performance

5. **user_droit** - User-Permission associations (Many-to-Many):
   - id (Integer, PK)
   - user_id (Integer, FK to users)
   - droit_id (Integer, FK to droit)
   - date_attribution (LocalDateTime) - When permission was assigned
   - date_expiration (LocalDateTime, nullable) - When permission expires
   - status (Boolean, default: true) - Active/inactive flag

### Relationships
```
User (N) ←──→ (1) Role (via id_role)
  │
  └── (N) ←──→ (N) Droit (via user_droit table)
      - Allows individual permission assignment
      - Supports expiration dates
      - Can be disabled independently

Role (N) ←──→ (N) Droit (via role_droit table)
  │
  └── Eagerly loaded to prevent lazy initialization exceptions
      - Users with ROLE_ADMIN get 22 droits
      - Users with ROLE_NORMAL_USER get 5 droits
      - Users with ROLE_CLIENT get 2 droits
```

## Authorization Service

The `AuthorizationService` provides utility methods for programmatic authorization checks. The application now has a complete RBAC system with role and permission (droit) management.

```java
// Get current authenticated user
UserDetailsImpl currentUser = authorizationService.getCurrentUser();

// Check user role
if (currentUser.getRoles().contains("ROLE_ADMIN")) {
    // User is admin with 22 permissions
}

// Check user permissions via user_droit table
```

### Permission Hierarchy
- **Role-Level**: Users get base permissions from their assigned role (id_role)
- **User-Level**: Individual permissions can be assigned via user_droit table (overrides/extends role permissions)
- **Droit Assignment**: Can include expiration dates (date_expiration) for time-limited permissions
- **Status Control**: Permissions can be disabled without deletion (status field)

## Method-Level Security

Use Spring Security annotations for method-level authorization:

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteUser(Long id) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public ResponseEntity<?> updateUser(User user) { ... }

@PreAuthorize("hasAuthority('PERM_USER_CREATE')")
public ResponseEntity<?> createUser(User user) { ... }
```

## Security Flow

### Authentication Flow
1. User sends credentials to `/api/auth/login`
2. Credentials validated against database
3. JWT token generated with user claims
4. Token sent in response with expiration time
5. Client stores token (localStorage, sessionStorage, etc.)

### Authorization Flow
1. Client includes token in `Authorization: Bearer <token>` header
2. `JwtAuthenticationFilter` intercepts request
3. Token signature validated using JWT secret
4. Token expiration checked
5. User details extracted from token claims
6. `UserDetailsService` loads User from database by login
7. User's authorities determined (currently empty list, can be extended with id_role)
8. `SecurityContext` populated with user and authorities
9. Endpoint access evaluated (currently roles-based via SecurityConfig)
10. Request allowed or denied

## Configuration

### application.properties
```properties
# JWT Configuration
app.jwtSecret=mySecureSecretKeyThatIsAtLeast32CharactersLongForHS256Algorithm
app.jwtExpirationMs=86400000

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/demo
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

**Important Security Notes:**
1. Change `app.jwtSecret` to a strong, random key in production
2. Change database credentials to secure values
3. Use `ddl-auto=update` in production (not `create-drop`)
4. Always use HTTPS in production
5. Keep JWT secret secure - regenerate if compromised

## API Authentication

All protected endpoints require JWT token in header:

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
     http://localhost:8080/api/organizations
```

## Example Setup

### 1. Register User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "login": "david",
  "email": "david@example.com",
  "pwd": "Password123!"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "david",
  "email": "david@example.com",
  "roles": [],
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "type": "Bearer"
}
```

### 2. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "login": "david",
  "pwd": "Password123!"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "david",
  "email": "david@example.com",
  "roles": [],
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "type": "Bearer"
}
```

### 3. Use Token for Protected Endpoints
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
     http://localhost:8080/api/protected
```

## Admin API Endpoints

### User Management

#### 1. Get User Permissions/Droits
```bash
GET /api/admin/users/{userId}/droits
Authorization: Bearer <token>
```
**Response:**
```json
{
  "droits": [
    {
      "id": 1,
      "droit": {
        "id": 1,
        "code": "USER_ENABLE_DISABLE",
        "libelle": "Enable or disable user account",
        "description": "Allow admin to activate/deactivate user accounts"
      },
      "dateAttribution": "2026-02-26T15:10:40.724897",
      "dateExpiration": null,
      "status": true
    }
  ]
}
```

#### 2. Assign Role to User
```bash
POST /api/admin/users/{userId}/assign-role/{roleId}
Authorization: Bearer <token>
Content-Type: application/json

{}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/3/assign-role/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response:**
```json
{
  "message": "Role assigned to user successfully",
  "userId": 3,
  "roleId": 1,
  "roleName": "ROLE_ADMIN"
}
```

#### 3. Remove Role from User
```bash
POST /api/admin/users/{userId}/remove-role
Authorization: Bearer <token>
Content-Type: application/json

{
  "roleId": 1
}
```
**Important:** You must specify which role to remove via `roleId` in the request body.

**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/3/remove-role \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"roleId": 1}'
```

**Response (Success):**
```json
{
  "message": "Role removed from user successfully",
  "userId": 3,
  "roleId": 1,
  "roleName": "ROLE_ADMIN"
}
```

**Response (User doesn't have this role):**
```json
{
  "message": "User does not have this role assigned",
  "userId": 3,
  "roleId": 1,
  "currentRoleId": 2
}
```

**Note:** The endpoint verifies the user currently has the specified role before removing it.

#### 4. Assign Permission (Droit) to User
```bash
POST /api/admin/users/{userId}/assign-droit/{droitId}
Authorization: Bearer <token>
Content-Type: application/json

{}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/3/assign-droit/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response:**
```json
{
  "message": "Permission assigned to user successfully",
  "userId": 3,
  "droitId": 5
}
```

#### 5. Remove Permission (Droit) from User
```bash
DELETE /api/admin/users/{userId}/remove-droit/{droitId}
Authorization: Bearer <token>
```
**Note:** Specify which droit to remove via {droitId} in the URL path.

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/admin/users/3/remove-droit/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

**Response:**
```json
{
  "message": "Permission removed from user successfully",
  "userId": 3,
  "droitId": 5
}
```

#### 6. Set User Status (Enable/Disable)
```bash
POST /api/admin/users/{userId}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": false
}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/3/status \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"status": false}'
```

**Response:**
```json
{
  "message": "User status updated successfully",
  "userId": 3,
  "status": false
}
```

#### 7. Reset User Password
```bash
POST /api/admin/users/{userId}/reset-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "newPassword": "NewPassword123!"
}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/3/reset-password \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"newPassword": "newpass456"}'
```

**Response:**
```json
{
  "message": "Password reset successfully",
  "userId": 3
}
```

### Role Management

#### 1. Get All Roles
```bash
GET /api/admin/roles
Authorization: Bearer <token>
```
**Response:**
```json
{
  "roles": [
    {
      "id": 1,
      "code": "ROLE_ADMIN",
      "libelle": "Administrator",
      "description": "Full system access with all permissions",
      "droit_count": 22,
      "sample_droits": ["USER_ENABLE_DISABLE", "USER_PASSWORD_RESET", ...]
    },
    {
      "id": 2,
      "code": "ROLE_NORMAL_USER",
      "libelle": "Normal User",
      "description": "Regular user with limited permissions",
      "droit_count": 5,
      "sample_droits": ["USER_READ", "ROLE_READ", ...]
    },
    {
      "id": 3,
      "code": "ROLE_CLIENT",
      "libelle": "Client",
      "description": "Client with minimal permissions",
      "droit_count": 2,
      "sample_droits": ["USER_READ", "VIEW_DASHBOARD"]
    }
  ]
}
```

#### 2. Get Single Role with All Droits
```bash
GET /api/admin/roles/{roleId}
Authorization: Bearer <token>
```
**Example:**
```bash
curl http://localhost:8080/api/admin/roles/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

**Response:**
```json
{
  "id": 1,
  "code": "ROLE_ADMIN",
  "libelle": "Administrator",
  "description": "Full system access with all permissions",
  "droits": [
    {"id": 1, "code": "USER_ENABLE_DISABLE", "libelle": "Enable or disable user account", ...},
    {"id": 2, "code": "USER_PASSWORD_RESET", "libelle": "Reset user password", ...},
    ...21 more permissions...
  ]
}
```

#### 3. Create New Role
```bash
POST /api/admin/roles
Authorization: Bearer <token>
Content-Type: application/json

{
  "code": "ROLE_MANAGER",
  "libelle": "Manager",
  "description": "Manager role with team management permissions"
}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/roles \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ROLE_MANAGER",
    "libelle": "Manager",
    "description": "Manager role for team management"
  }'
```

**Response:**
```json
{
  "id": 4,
  "code": "ROLE_MANAGER",
  "libelle": "Manager",
  "description": "Manager role for team management"
}
```

#### 4. Assign Permission to Role
```bash
POST /api/admin/roles/{roleId}/assign-droit/{droitId}
Authorization: Bearer <token>
Content-Type: application/json

{}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/roles/2/assign-droit/10 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response:**
```json
{
  "message": "Permission assigned to role successfully",
  "roleId": 2,
  "droitId": 10
}
```

### Permission Management (Droits)

#### 1. Get All Permissions
```bash
GET /api/admin/droits
Authorization: Bearer <token>
```
**Response contains all 22 permissions:**
```json
{
  "droits": [
    {
      "id": 1,
      "code": "USER_ENABLE_DISABLE",
      "libelle": "Enable or disable user account",
      "description": "Allow admin to activate/deactivate user accounts"
    },
    ... (21 more items)
  ]
}
```

#### 2. Get Single Permission
```bash
GET /api/admin/droits/{droitId}
Authorization: Bearer <token>
```
**Example:**
```bash
curl http://localhost:8080/api/admin/droits/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

**Response:**
```json
{
  "id": 5,
  "code": "USER_ASSIGN_ROLE",
  "libelle": "Assign roles to users",
  "description": "Permission to assign roles to user accounts"
}
```

#### 3. Create New Permission
```bash
POST /api/admin/droits
Authorization: Bearer <token>
Content-Type: application/json

{
  "code": "CUSTOM_PERMISSION",
  "libelle": "Custom Permission",
  "description": "Description of custom permission"
}
```
**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/droits \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "code": "REPORT_EXPORT",
    "libelle": "Export Reports",
    "description": "Permission to export reports to CSV/PDF"
  }'
```

**Response:**
```json
{
  "id": 23,
  "code": "REPORT_EXPORT",
  "libelle": "Export Reports",
  "description": "Permission to export reports to CSV/PDF"
}
```

## Security Best Practices

1. **JWT Secret**: Use strong, random secret key (32+ characters)
2. **HTTPS**: Always use HTTPS in production
3. **Token Storage**: Store tokens securely (not in localStorage if possible)
4. **CORS**: Validate origin headers on production
5. **Rate Limiting**: Implement rate limiting on auth endpoints
6. **Password Policy**: Enforce strong passwords
7. **Audit Logging**: Log authentication and authorization events
8. **Regular Audits**: Review role/permission assignments regularly
9. **Token Rotation**: Consider implementing token refresh mechanism
10. **Database Security**: Use strong database credentials and encryption

## Troubleshooting

### Authentication Issues
- Check request body uses `login` and `pwd` (not username/password)
- Verify user exists in database with matching login
- Verify password is correct (compared against bcrypt hash)

### Token Validation Issues
- Check `app.jwtSecret` consistency between requests
- Verify token format: `Authorization: Bearer <token>`
- Check token expiration time (24 hours from issuance)
- Ensure token was generated by the same service that validates it

### User Registration Issues
- Verify login is unique (not already registered)
- Verify email is unique (not already registered)
- Check password meets security requirements

### Database Issues
- Verify PostgreSQL is running on localhost:5432
- Check database name is "demo" (or configured correctly)
- Ensure user table schema has all columns: id, login, email, pwd, id_role, libelle, date_creation, date_desactivation, status

## Future Enhancements
1. Token refresh mechanism
2. Audit logging system (track all role/permission changes)
3. Two-factor authentication (2FA)
4. Rate limiting on authentication endpoints
5. Account deactivation enforcement (using date_desactivation field)
6. Password reset/recovery flow with email verification
7. Email verification on registration
8. Account lockout after failed login attempts
9. Session management and token revocation
10. Method-level security with `@PreAuthorize` annotations

## Implementation Notes

### Current State
- JWT authentication fully functional
- Complete RBAC system with 3 roles and 22 permissions
- User-role and user-droit assignment working
- Admin endpoints for managing users, roles, and permissions
- Database auto-initialization with seed data
- Permission expiration support (via date_expiration field)
- Permission status control (can disable without deletion)

### What's Implemented
✅ User registration (`POST /api/auth/register`)
✅ User login (`POST /api/auth/login`)
✅ JWT token generation (24-hour expiration, HS384 algorithm)
✅ Token validation on protected endpoints
✅ Password encryption (BCrypt)
✅ RBAC with 3 roles: ROLE_ADMIN, ROLE_NORMAL_USER, ROLE_CLIENT
✅ 22 permissions (droits) across 4 categories
✅ Role management API (create, read, list, assign permissions)
✅ Permission management API (create, read, list)
✅ User role assignment/removal
✅ User direct permission assignment (user_droit table)
✅ User enable/disable status management
✅ User password reset via admin
✅ Auto-seeding of roles and permissions on startup

### What Can Be Extended
- Method-level security with `@PreAuthorize` annotations
- Audit logging for all admin operations
- Permission expiration enforcement (background job)
- Custom role creation and management
- Multi-factor authentication (MFA)
- API rate limiting
- GraphQL API for complex queries

## API Quick Reference

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/api/admin/users/{userId}/droits` | Get user permissions |
| POST | `/api/admin/users/{userId}/assign-role/{roleId}` | Assign role to user |
| POST | `/api/admin/users/{userId}/remove-role` | Remove role from user |
| POST | `/api/admin/users/{userId}/assign-droit/{droitId}` | Assign permission to user |
| DELETE | `/api/admin/users/{userId}/remove-droit/{droitId}` | Remove permission from user |
| POST | `/api/admin/users/{userId}/status` | Enable/disable user |
| POST | `/api/admin/users/{userId}/reset-password` | Reset user password |
| GET | `/api/admin/roles` | List all roles |
| GET | `/api/admin/roles/{roleId}` | Get role with permissions |
| POST | `/api/admin/roles` | Create new role |
| POST | `/api/admin/roles/{roleId}/assign-droit/{droitId}` | Assign permission to role |
| GET | `/api/admin/droits` | List all permissions |
| GET | `/api/admin/droits/{droitId}` | Get permission details |
| POST | `/api/admin/droits` | Create new permission |
