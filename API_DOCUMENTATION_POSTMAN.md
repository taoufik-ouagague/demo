# Complete API Documentation for Postman Testing - UPDATED

## Base URL
```
http://localhost:8080
```

---

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [User APIs](#user-apis)
3. [Role APIs](#role-apis)
4. [Droit (Permission) APIs](#droit-apis)
5. [Role-Droit Management APIs](#role-droit-management-apis)
6. [User-Droit Management APIs](#user-droit-management-apis)
7. [Role-User Management APIs](#role-user-management-apis)
8. [Test Users and Credentials](#test-users-and-credentials)

---

## Authentication APIs

### 1. Login
**Method:** `POST`
**Endpoint:** `/api/auth/login`
**Authentication:** None (Public)
**Authorization:** None

**Request Body:**
```json
{
  "login": "admin",
  "pwd": "Admin@123"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": [],
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "type": "Bearer"
}
```

---

## User APIs

All user endpoints require: **`@RequireAdminRole`** - User must have ROLE_ADMIN

### 1. ListUsers
**Method:** `GET`
**Endpoint:** `/api/admin/ListUsers`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Query Parameters (Optional):**
- `libelle` - Filter by user full name
- `login` - Filter by login username
- `status` - Filter by status

**Example Request:**
```
GET http://localhost:8080/api/admin/ListUsers?login=admin&status=Y
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "total": 2,
  "users": [
    {
      "id": 1,
      "login": "admin",
      "email": "admin@example.com",
      "libelle": null,
      "status": "Y",
      "idRole": 1
    }
  ]
}
```

---

### 2. GetUser
**Method:** `GET`
**Endpoint:** `/api/admin/GetUser/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of the user to retrieve

**Example Request:**
```
GET http://localhost:8080/api/admin/GetUser/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "login": "admin",
  "email": "admin@example.com",
  "status": "Y"
}
```

---

### 3. AddUser
**Method:** `POST`
**Endpoint:** `/api/admin/AddUser`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Request Body:**
```json
{
  "login": "newuser",
  "email": "newuser@example.com",
  "libelle": "New User",
  "idRole": 2
}
```

**Response (200 OK):**
```json
{
  "message": "Utilisateur créé avec succès",
  "user": {
    "id": 5,
    "login": "newuser",
    "email": "newuser@example.com",
    "status": "1"
  }
}
```

---

### 4. UpdateUser
**Method:** `POST`
**Endpoint:** `/api/admin/UpdateUser/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user to update

**Request Body (All fields optional):**
```json
{
  "email": "newemail@example.com",
  "libelle": "Updated Name"
}
```

**Response (200 OK):**
```json
{
  "message": "Utilisateur mis à jour avec succès",
  "user": {
    "id": 5,
    "login": "newuser",
    "email": "newemail@example.com"
  }
}
```

---

### 5. DeleteUser
**Method:** `POST`
**Endpoint:** `/api/admin/DeleteUser/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user to delete

**Example Request:**
```
POST http://localhost:8080/api/admin/DeleteUser/5
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Utilisateur supprimé avec succès",
  "userId": 5
}
```

---

### 6. UpdateUserStatus
**Method:** `POST`
**Endpoint:** `/api/admin/UpdateUserStatus/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user

**Request Body:**
```json
{
  "status": false
}
```

**Response (200 OK):**
```json
{
  "message": "Utilisateur désactivé"
}
```

---

### 7. ResetUserPassword
**Method:** `POST`
**Endpoint:** `/api/admin/ResetUserPassword/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** @RequirePermission("USER_PASSWORD_RESET")

**Path Parameters:**
- `userId` - ID of user

**Example Request:**
```
POST http://localhost:8080/api/admin/ResetUserPassword/5
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Mot de passe de l'utilisateur a été réinitialisé."
}
```

---

## Role APIs

All role endpoints require: **`@RequireAdminRole`** - User must have ROLE_ADMIN

### 1. ListRoles
**Method:** `GET`
**Endpoint:** `/api/admin/ListRoles`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Query Parameters (Optional):**
- `code` - Filter by role code
- `libelle` - Filter by role name
- `status` - Filter by status

**Example Request:**
```
GET http://localhost:8080/api/admin/ListRoles?code=ADMIN
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "total": 3,
  "roles": [
    {
      "id": 1,
      "code": "ROLE_ADMIN",
      "libelle": "Administrateur",
      "description": "Full access",
      "status": null
    }
  ]
}
```

---

### 2. GetRole
**Method:** `GET`
**Endpoint:** `/api/admin/GetRole/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `roleId` - ID of the role to retrieve

**Example Request:**
```
GET http://localhost:8080/api/admin/GetRole/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "code": "ROLE_ADMIN",
  "libelle": "Administrateur",
  "description": "Full access"
}
```

---

### 3. AddRole
**Method:** `POST`
**Endpoint:** `/api/admin/AddRole`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Request Body:**
```json
{
  "code": "ROLE_MANAGER",
  "libelle": "Manager",
  "description": "Manager role"
}
```

**Response (200 OK):**
```json
{
  "message": "Rôle créé avec succès",
  "role": {
    "id": 5,
    "code": "ROLE_MANAGER",
    "libelle": "Manager",
    "status": "1"
  }
}
```

---

### 4. UpdateRole
**Method:** `POST`
**Endpoint:** `/api/admin/UpdateRole/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `roleId` - ID of role to update

**Request Body (All fields optional):**
```json
{
  "libelle": "Updated Role Name",
  "description": "Updated description"
}
```

**Response (200 OK):**
```json
{
  "message": "Rôle mis à jour avec succès",
  "role": {
    "id": 5,
    "code": "ROLE_MANAGER",
    "libelle": "Updated Role Name"
  }
}
```

---

### 5. DeleteRole
**Method:** `POST`
**Endpoint:** `/api/admin/DeleteRole/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `roleId` - ID of role to delete

**Example Request:**
```
POST http://localhost:8080/api/admin/DeleteRole/5
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Rôle supprimé avec succès",
  "roleId": 5
}
```

---

## Droit APIs

All droit endpoints require: **`@RequireAdminRole`** - User must have ROLE_ADMIN

### 1. ListDroits
**Method:** `GET`
**Endpoint:** `/api/admin/ListDroits`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Query Parameters (Optional):**
- `code` - Filter by permission code
- `libelle` - Filter by permission name
- `status` - Filter by status

**Example Request:**
```
GET http://localhost:8080/api/admin/ListDroits?code=USER_READ
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "total": 23,
  "droits": [
    {
      "id": 8,
      "code": "USER_READ",
      "libelle": "Read user info",
      "description": "Permission to view user information"
    }
  ]
}
```

---

### 2. GetDroit
**Method:** `GET`
**Endpoint:** `/api/admin/GetDroit/{droitId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `droitId` - ID of the droit to retrieve

**Example Request:**
```
GET http://localhost:8080/api/admin/GetDroit/8
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 8,
  "code": "USER_READ",
  "libelle": "Read user info",
  "description": "Permission to view user information"
}
```

---

### 3. AddDroit
**Method:** `POST`
**Endpoint:** `/api/admin/AddDroit`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Request Body:**
```json
{
  "code": "CUSTOM_PERMISSION",
  "libelle": "Custom Permission",
  "description": "A custom permission"
}
```

**Response (200 OK):**
```json
{
  "message": "Droit créé avec succès",
  "droit": {
    "id": 26,
    "code": "CUSTOM_PERMISSION",
    "libelle": "Custom Permission",
    "status": "1"
  }
}
```

---

### 4. UpdateDroit
**Method:** `POST`
**Endpoint:** `/api/admin/UpdateDroit/{droitId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `droitId` - ID of droit to update

**Request Body:**
```json
{
  "libelle": "Updated Permission",
  "description": "Updated description"
}
```

**Response (200 OK):**
```json
{
  "message": "Droit mis à jour avec succès",
  "droit": {
    "id": 26,
    "code": "CUSTOM_PERMISSION",
    "libelle": "Updated Permission"
  }
}
```

---

### 5. DeleteDroit
**Method:** `POST`
**Endpoint:** `/api/admin/DeleteDroit/{droitId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `droitId` - ID of droit to delete

**Example Request:**
```
POST http://localhost:8080/api/admin/DeleteDroit/26
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Droit supprimé avec succès",
  "droitId": 26
}
```

---

## Role-Droit Management APIs

### 1. GetRoleDroits
**Method:** `GET`
**Endpoint:** `/api/admin/GetRoleDroits/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** @RequirePermission("ROLE_DROIT_MANAGEMENT")

**Path Parameters:**
- `roleId` - ID of role

**Example Request:**
```
GET http://localhost:8080/api/admin/GetRoleDroits/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "roleId": 1,
  "roleName": "Administrateur",
  "assignedDroits": [
    {
      "id": 1,
      "code": "USER_ENABLE_DISABLE",
      "libelle": "Enable/disable user accounts"
    }
  ],
  "notAssignedDroits": [
    {
      "id": 2,
      "code": "USER_PASSWORD_RESET",
      "libelle": "Reset user password"
    }
  ],
  "totalAssigned": 22,
  "totalNotAssigned": 1
}
```

---

### 2. ManageRoleDroits
**Method:** `POST`
**Endpoint:** `/api/admin/ManageRoleDroits/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** @RequirePermission("ROLE_DROIT_MANAGEMENT")

**Path Parameters:**
- `roleId` - ID of role

**Request Body:**
```json
{
  "action": "add",
  "droitIds": [5, 6, 7]
}
```

**Response (200 OK) - Success:**
```json
{
  "message": "3 droit(s) assignés au rôle avec succès",
  "action": "add",
  "roleId": 1,
  "roleName": "Administrateur",
  "processedDroitIds": [5, 6, 7],
  "errors": null
}
```

---

## User-Droit Management APIs

All user-droit endpoints require: **`@RequireAdminRole`**

### 1. AssignDroitToUser
**Method:** `POST`
**Endpoint:** `/api/admin/AssignDroitToUser/{droitId}/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `droitId` - ID of droit
- `userId` - ID of user

**Example Request:**
```
POST http://localhost:8080/api/admin/AssignDroitToUser/8/2
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Droit assigné à l'utilisateur avec succès",
  "userId": 2,
  "droitId": 8,
  "droitCode": "USER_READ",
  "droitLibelle": "Read user info"
}
```

---

### 2. RemoveDroitFromUser
**Method:** `POST`
**Endpoint:** `/api/admin/RemoveDroitFromUser/{droitId}/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `droitId` - ID of droit
- `userId` - ID of user

**Example Request:**
```
POST http://localhost:8080/api/admin/RemoveDroitFromUser/8/2
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Droit supprimé de l'utilisateur avec succès",
  "userId": 2,
  "droitId": 8
}
```

---

### 3. GetUserDroits
**Method:** `GET`
**Endpoint:** `/api/admin/GetUserDroits/{userId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user

**Example Request:**
```
GET http://localhost:8080/api/admin/GetUserDroits/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "login": "admin",
  "droits": [
    {
      "id": 1,
      "user": {...},
      "droit": {
        "id": 1,
        "code": "USER_ENABLE_DISABLE",
        "libelle": "Enable/disable user accounts"
      }
    }
  ]
}
```

---

## Role-User Management APIs

All role-user endpoints require: **`@RequireAdminRole`**

### 1. AssignRoleToUser
**Method:** `POST`
**Endpoint:** `/api/admin/AssignRoleToUser/{userId}/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user
- `roleId` - ID of role

**Example Request:**
```
POST http://localhost:8080/api/admin/AssignRoleToUser/2/2
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Rôle assigné à l'utilisateur avec succès",
  "userId": 2,
  "roleId": 2,
  "roleName": "Normal User"
}
```

---

### 2. RemoveRoleFromUser
**Method:** `POST`
**Endpoint:** `/api/admin/RemoveRoleFromUser/{userId}/{roleId}`
**Authentication:** Bearer Token (Required)
**Authorization:** ROLE_ADMIN (Required)

**Path Parameters:**
- `userId` - ID of user
- `roleId` - ID of role

**Example Request:**
```
POST http://localhost:8080/api/admin/RemoveRoleFromUser/2/2
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "message": "Rôle supprimé de l'utilisateur avec succès",
  "userId": 2,
  "roleId": 2,
  "roleName": "Normal User"
}
```

---

## Copy-Paste Curl Examples - UPDATED ENDPOINTS

### 1. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq '.token' -r
```

### 2. List All Users
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X GET "http://localhost:8080/api/admin/ListUsers" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 3. Create New User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/AddUser" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "login":"newuser",
    "email":"newuser@example.com",
    "libelle":"New Test User",
    "idRole":2
  }' | jq '.'
```

### 4. Update User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/UpdateUser/2" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email":"updated@example.com"}' | jq '.'
```

### 5. Delete User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/DeleteUser/2" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 6. List Roles
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X GET "http://localhost:8080/api/admin/ListRoles" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```
  
### 7. Create Role
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/AddRole" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code":"ROLE_MANAGER",
    "libelle":"Manager Role",
    "description":"Role for managers"
  }' | jq '.'
```

### 8. Delete Role
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/DeleteRole/1" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 9. List Droits
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X GET "http://localhost:8080/api/admin/ListDroits" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 10. Create Droit
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/AddDroit" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code":"CUSTOM_PERMISSION",
    "libelle":"Custom Permission",
    "description":"A custom permission"
  }' | jq '.'
```

### 11. Delete Droit
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/DeleteDroit/1" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 12. Assign Droit to User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/AssignDroitToUser/1/2" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 13. Get Role Droits
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X GET "http://localhost:8080/api/admin/GetRoleDroits/1" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

### 14. Batch Assign Droits to Role
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/ManageRoleDroits/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "action":"add",
    "droitIds":[1,2,3]
  }' | jq '.'
```

### 15. Assign Role to User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","pwd":"Admin@123"}' | jq -r '.token') && \
curl -s -X POST "http://localhost:8080/api/admin/AssignRoleToUser/2/2" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

---

## Test Users and Credentials

| Login | Password | Role | Status |
|-------|----------|------|--------|
| admin | Admin@123 | ROLE_ADMIN | Active |
| USER | Admin@123 | ROLE_NORMAL_USER | Active |

---

## Available Roles
- ID 1: ROLE_ADMIN (Administrateur)
- ID 2: ROLE_NORMAL_USER (Utilisateur Normal)
- ID 3: ROLE_CLIENT (Client)

---

## Notes for Postman Users

1. **Create Environment Variables:**
   - `baseUrl` = `http://localhost:8080`
   - `token` = Gets set from login response

2. **Use {{token}}** in Authorization headers for all protected endpoints

3. **Global Headers:**
   ```
   Content-Type: application/json
   Authorization: Bearer {{token}}
   ```

