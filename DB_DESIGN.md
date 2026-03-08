# Database Design - 6 Tables

## 📊 Overview
- **Database:** demo1 (PostgreSQL)
- **Tables:** 6
- **Architecture:** Role-Based Access Control (RBAC) with Permissions
- **Purpose:** User authentication, role management, and permission control

---

## 🏗️ Database Schema

### 1. **users**
Stores user account information and credentials.

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR UNIQUE NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    pwd VARCHAR NOT NULL,
    id_role INTEGER,
    libelle VARCHAR,
    code VARCHAR(20),
    status VARCHAR(3),
    date_creation DATE,
    date_modification DATE,
    date_desactivation DATE,
    FOREIGN KEY (id_role) REFERENCES roles(id)
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | User unique identifier |
| login | VARCHAR | UNIQUE, NOT NULL | Username for login |
| email | VARCHAR | UNIQUE, NOT NULL | User email address |
| pwd | VARCHAR | NOT NULL | Hashed password |
| id_role | INTEGER | FK → roles.id | Primary role reference |
| libelle | VARCHAR | | User display name |
| code | VARCHAR(20) | | User code |
| status | VARCHAR(3) | | Account status (Y/N) |
| date_creation | DATE | | Account creation date |
| date_modification | DATE | | Last modification date |
| date_desactivation | DATE | | Account deactivation date |

---

### 2. **roles**
Defines role definitions with code and description.

```sql
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR NOT NULL,
    description VARCHAR,
    status VARCHAR(3),
    date_creation DATE,
    date_modification DATE,
    date_desactivation DATE
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Role unique identifier |
| code | VARCHAR(20) | UNIQUE, NOT NULL | Role code (ROLE_ADMIN, ROLE_USER, ROLE_CLIENT) |
| libelle | VARCHAR | NOT NULL | Role display name (Administrateur, Utilisateur Normal, Client) |
| description | VARCHAR | | Role description |
| status | VARCHAR(3) | | Role status (Y/N) |
| date_creation | DATE | | Creation date |
| date_modification | DATE | | Last modification date |
| date_desactivation | DATE | | Deactivation date |

**Pre-configured Roles:**
- `ROLE_ADMIN` → Administrateur (Full access)
- `ROLE_NORMAL_USER` → Utilisateur Normal (Limited access)
- `ROLE_CLIENT` → Client (Restricted access)

---

### 3. **droit**
Stores detailed permissions/rights definitions.

```sql
CREATE TABLE droit (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR NOT NULL,
    description VARCHAR,
    status VARCHAR(3),
    date_creation DATE,
    date_modification DATE,
    date_desactivation DATE
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Permission unique identifier |
| code | VARCHAR(20) | UNIQUE, NOT NULL | Permission code (e.g., USER_CREATE, USER_READ, ROLE_UPDATE) |
| libelle | VARCHAR | NOT NULL | Permission display name |
| description | VARCHAR | | Permission description |
| status | VARCHAR(3) | | Permission status (Y/N) |
| date_creation | DATE | | Creation date |
| date_modification | DATE | | Last modification date |
| date_desactivation | DATE | | Deactivation date |

**Sample Permissions:**
- User Management: `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `USER_ENABLE_DISABLE`
- Role Management: `ROLE_CREATE`, `ROLE_READ`, `ROLE_UPDATE`, `ROLE_DELETE`, `ROLE_ASSIGN_DROIT`
- Permission Management: `DROIT_CREATE`, `DROIT_READ`, `DROIT_UPDATE`, `DROIT_DELETE`
- System Access: `ADMIN_ACCESS`, `VIEW_DASHBOARD`, `VIEW_REPORTS`, `CONTENT_MODERATION`

---

### 4. **role_droit**
Junction table linking roles with permissions.

```sql
CREATE TABLE role_droit (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL,
    droit_id INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (droit_id) REFERENCES droit(id),
    UNIQUE(role_id, droit_id)
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Record unique identifier |
| role_id | INTEGER | FK → roles.id | Role reference |
| droit_id | INTEGER | FK → droit.id | Permission reference |

**Purpose:** Maps permissions to roles (many-to-many relationship)

**Mappings:**
- **ROLE_ADMIN:** 21 permissions (all operations)
- **ROLE_NORMAL_USER:** 6 permissions (read + dashboard + reports)
- **ROLE_CLIENT:** 2 permissions (read + dashboard)

---

### 5. **user_droit**
Junction table linking users with individual permissions.

```sql
CREATE TABLE user_droit (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    droit_id INTEGER NOT NULL,
    role_id INTEGER,
    status BOOLEAN DEFAULT true,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (droit_id) REFERENCES droit(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Record unique identifier |
| user_id | INTEGER | FK → users.id | User reference |
| droit_id | INTEGER | FK → droit.id | Permission reference |
| role_id | INTEGER | FK → roles.id | Associated role |
| status | BOOLEAN | DEFAULT true | Permission active/inactive |
| date_debut | TIMESTAMP | | Permission start date |
| date_fin | TIMESTAMP | | Permission end date |

**Purpose:** 
- Assigns permissions directly to users (independent of role)
- Allows permission expiration
- Tracks temporary or role-specific permissions

---

### 6. **organizations**
Organizational structure support.

```sql
CREATE TABLE organizations (
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL,
    description VARCHAR,
    status VARCHAR(3),
    date_creation DATE,
    date_modification DATE,
    date_desactivation DATE
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | SERIAL | PK, AUTO_INCREMENT | Organization unique identifier |
| name | VARCHAR | UNIQUE, NOT NULL | Organization name |
| description | VARCHAR | | Organization description |
| status | VARCHAR(3) | | Organization status (Y/N) |
| date_creation | DATE | | Creation date |
| date_modification | DATE | | Last modification date |
| date_desactivation | DATE | | Deactivation date |

**Purpose:** Support for multi-tenancy and organizational structure

---

## 🔗 Entity Relationships

```
USERS (1) ──── id_role ───> ROLES (N)
  │
  └──> user_droit (N) ───┐
                         ├──> DROIT
                         └──> role_droit (N) ──┘

ROLES (1) ──── role_droit (N) ───> DROIT

ORGANIZATIONS (separate entity)
```

## 📋 Permission Flow

```
Step 1: User has a Primary Role (via users.id_role)
   USER → users.id_role → ROLE

Step 2: Role grants Permissions (via role_droit)
   ROLE → role_droit → DROIT

Step 3: User can have Additional Permissions (via user_droit)
   USER → user_droit → DROIT
```

## 🎯 Default Data

### Roles
```
┌─────────────────────────────────────────────────────────────┐
│ Role         │ Code              │ Permissions               │
├─────────────────────────────────────────────────────────────┤
│ Administrateur│ ROLE_ADMIN        │ 21 permissions (all)    │
│ Utilisateur N.│ ROLE_NORMAL_USER  │ 6 permissions (read)    │
│ Client       │ ROLE_CLIENT       │ 2 permissions (basic)   │
└─────────────────────────────────────────────────────────────┘
```

### Users
```
┌──────────────────────────────────────┐
│ Login  │ Email               │ Role   │
├──────────────────────────────────────┤
│ admin  │ admin@example.com   │ ADMIN  │
│ USER   │ user@example.com    │ NORMAL │
└──────────────────────────────────────┘
```

**Default Passwords:** `Admin@123`

---

## 📊 Data Statistics

| Table | Records | Purpose |
|-------|---------|---------|
| users | 2+ | User accounts (admin, USER) |
| roles | 3 | ADMIN, NORMAL_USER, CLIENT |
| droit | 23+ | Detailed permissions |
| role_droit | 29+ | Role-permission mappings |
| user_droit | 5+ | User-permission assignments |
| organizations | 0+ | Organizational units |

---

## 🔐 Security Features

✅ **Password Encryption**
- Passwords stored as bcrypt hashes
- `pwd` column contains encoded values

✅ **Permission Inheritance**
- Users inherit permissions from their assigned role
- Can have additional permissions via user_droit
- Permissions can be active/inactive (soft delete)

✅ **Temporal Permissions**
- `date_debut` and `date_fin` for time-limited access
- `status` flags for soft deletes

✅ **Audit Trail**
- `date_creation`, `date_modification`, `date_desactivation`
- Tracks all entity lifecycle events

✅ **Unique Constraints**
- login, email (users)
- code (roles, droit)
- Prevents duplicate data entry

---

## 🚀 Usage Examples

### Check User Permissions
```sql
-- Get all permissions for a user (including role-inherited)
SELECT DISTINCT d.code, d.libelle
FROM user_droit ud
JOIN droit d ON ud.droit_id = d.id
WHERE ud.user_id = ? AND ud.status = true

UNION

SELECT d.code, d.libelle
FROM users u
JOIN role_droit rd ON u.id_role = (SELECT id FROM roles WHERE id = u.id_role)
JOIN droit d ON rd.droit_id = d.id
WHERE u.id = ?;
```

### Assign Permission to Role
```sql
INSERT INTO role_droit (role_id, droit_id)
VALUES (?, ?)
ON CONFLICT DO NOTHING;
```

### Assign Permission to User
```sql
INSERT INTO user_droit (user_id, droit_id, role_id, status)
VALUES (?, ?, ?, true);
```

---

## 📝 Updated: March 3, 2026
