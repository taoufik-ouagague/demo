# PostgreSQL Database Schema - Complete Design

## Overview
**Database:** demo  
**Tables:** 9  
**Type:** Role-Based Access Control (RBAC) with Permissions Management

---

## Table Structure

### 1. **users**
Stores user account information and credentials.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | User unique identifier |
| login | VARCHAR | UNIQUE, NOT NULL | Username for login |
| email | VARCHAR | UNIQUE, NOT NULL | User email address |
| pwd | VARCHAR | NOT NULL | Encoded password |
| id_role | INTEGER | FK → roles.id | Primary role assignment |
| libelle | VARCHAR | | User display name |
| status | BOOLEAN | DEFAULT true | Account active/inactive |
| date_creation | TIMESTAMP | | Account creation date |
| date_desactivation | TIMESTAMP | | Account deactivation date |

---

### 2. **roles**
Defines role definitions with code and description.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Role unique identifier |
| code | VARCHAR | UNIQUE, NOT NULL | Role code (ROLE_ADMIN, etc.) |
| libelle | VARCHAR | NOT NULL | Role display name |
| description | VARCHAR | | Role description |
| name | VARCHAR(20) | | Role enum name |

---

### 3. **droit**
Stores detailed permissions/rights definitions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Permission unique identifier |
| code | VARCHAR | UNIQUE, NOT NULL | Permission code |
| libelle | VARCHAR | NOT NULL | Permission display name |
| description | VARCHAR | | Permission description |

**Note:** 538 rows of permissions mapped to roles

---

### 4. **permissions**
Simple permission reference table.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Permission unique identifier |
| name | VARCHAR(20) | NOT NULL | Permission name |

---

### 5. **user_roles**
Junction table for many-to-many user-role relationships.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | INTEGER | PK, FK → users.id | User reference |
| role_id | INTEGER | PK, FK → roles.id | Role reference |

**Purpose:** Allows users to have multiple roles

---

### 6. **user_droit**
Junction table linking users with individual permissions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Record unique identifier |
| user_id | INTEGER | FK → users.id | User reference |
| droit_id | INTEGER | FK → droit.id | Permission reference |
| status | BOOLEAN | DEFAULT true | Permission active/inactive |
| date_attribution | TIMESTAMP | | When permission was assigned |
| date_expiration | TIMESTAMP | | When permission expires |

**Purpose:** Granular permission control per user

---

### 7. **role_droit**
Junction table linking roles with permissions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| role_id | INTEGER | PK, FK → roles.id | Role reference |
| droit_id | INTEGER | PK, FK → droit.id | Permission reference |

**Purpose:** Maps permissions to roles (538+ mappings)

---

### 8. **role_permissions**
Junction table linking roles with simple permissions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| role_id | INTEGER | PK, FK → roles.id | Role reference |
| permission_id | INTEGER | PK, FK → permissions.id | Permission reference |

**Purpose:** Alternative permission mapping for roles

---

### 9. **organizations**
Organizational structure support.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Organization unique identifier |
| name | VARCHAR | UNIQUE, NOT NULL | Organization name |
| description | VARCHAR | | Organization description |

---

## Entity Relationships

```
USERS (1) ──────────── (N) USER_ROLES
  │                         │
  │                         ↓
  │                      ROLES (1)
  │                         │
  │                    ┌────┴────┐
  │                    │          │
  │                    ↓          ↓
  │              ROLE_DROIT   ROLE_PERMISSIONS
  │                    │          │
  │                    ↓          ↓
  │                 DROIT    PERMISSIONS
  │
  └──────────────── (1) USER_DROIT (N)
                       │
                       ↓
                    DROIT
```

---

## Permission Model

### Two-Level Permission System:

**Level 1: Role-Based Permissions**
- User → (via id_role) → Role → (via role_droit) → Droit
- User → (via role_droit direct) → Droit
- User → (via user_roles + role_droit) → Droit
- Role → (via role_permissions) → Permissions

**Level 2: User-Specific Permissions**
- User → (via user_droit) → Droit (Direct permissions regardless of role)

---

## Key Characteristics

✅ **Multiple Permission Definitions**
- `droit` table for detailed permissions (538+ rows)
- `permissions` table for simplified permissions
- Separate mapping tables for flexibility

✅ **Flexible Role Assignment**
- Users can have multiple roles via `user_roles`
- Primary role assignment via `users.id_role`
- Additional roles via junction table

✅ **Granular Permission Control**
- Role-level permissions via `role_droit`
- User-level permissions via `user_droit`
- Permission expiration support

✅ **Audit Trail**
- Date creation/deactivation for users
- Date attribution/expiration for permissions
- Status flags for soft deletes

✅ **Organizational Support**
- Organizations table for multi-tenancy preparation
- Extensible for future org-based RBAC

---

## Data Integrity Constraints

- **Unique Constraints:** login, email (users); code (roles, droit)
- **Foreign Keys:** Maintain referential integrity
- **NOT NULL:** Critical fields protected
- **Default Values:** status=true, auto-timestamps

---

## Statistics

| Table | Columns | Primary Key | Foreign Keys |
|-------|---------|------------|--------------|
| users | 9 | id | 1 (roles) |
| roles | 5 | id | - |
| droit | 4 | id | - |
| permissions | 2 | id | - |
| user_roles | 2 | (user_id, role_id) | 2 |
| user_droit | 6 | id | 2 |
| role_droit | 2 | (role_id, droit_id) | 2 |
| role_permissions | 2 | (role_id, permission_id) | 2 |
| organizations | 3 | id | - |

---

## Last Updated
**Date:** March 2, 2026  
**Tables:** 9  
**Total Columns:** 44  
**Total Rows:** 1000+ (including 538 role_droit mappings)

