# API Documentation - Complete Endpoint Reference

## Base URL
```
http://localhost:8080
```

## Authentication Endpoints

### 1. Login
**Endpoint:** `POST /api/auth/login`

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
  "id": 19,
  "username": "admin",
  "email": "admin@example.com",
  "roles": [],
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MjIwMTE2MiwiZXhwIjoxNzcyMjg3NTYyfQ.HORhaHBzbX6xWTRMsAVpNJ2I_VHFBNtkmrypCNgPn8q18E_i-2ynRGsb2uNPWhCh",
  "type": "Bearer"
}
```

**Error Response (400):**
```json
{
  "message": "Identifiants de connexion invalides",
  "error": "Bad credentials"
}
```

---

### 2. Register
**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "login": "newuser",
  "email": "newuser@example.com",
  "pwd": "Password@123"
}
```

**Response (200 OK):**
```json
{
  "id": 21,
  "username": "newuser",
  "email": "newuser@example.com",
  "roles": [],
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJuZXd1c2VyIiwiaWF0IjoxNzcyMjA0MTM1LCJleHAiOjE3NzIyOTA1MzV9...",
  "type": "Bearer"
}
```

**Error Response - Duplicate Login (400):**
```json
{
  "message": "Erreur : Le login est déjà utilisé !"
}
```

**Error Response - Duplicate Email (400):**
```json
{
  "message": "Erreur : L'email est déjà utilisé !"
}
```

---

### 3. Get Current User
**Endpoint:** `GET /api/auth/me`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "id": 19,
  "username": "admin",
  "email": "admin@example.com",
  "authorities": [
    {
      "authority": "ROLE_ADMIN"
    }
  ]
}
```

**Error Response (401 Unauthorized):**
```json
```

**Note:** This endpoint returns 401 status without a response body if the user is not authenticated.

---

## Admin Endpoints (Require Bearer Token)

### USER MANAGEMENT

#### 3. Get User Droits (Permissions)
**Endpoint:** `GET /api/admin/users/{userId}/droits`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "userId": 19,
  "login": "admin",
  "droits": [
    {
      "id": 1,
      "droit": {
        "id": 1,
        "code": "USER_ENABLE_DISABLE",
        "libelle": "Activer ou désactiver le compte utilisateur",
        "description": "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs"
      },
      "status": true
    },
    {
      "id": 2,
      "droit": {
        "id": 2,
        "code": "USER_PASSWORD_RESET",
        "libelle": "Réinitialiser le mot de passe utilisateur",
        "description": "Permettre à l'administrateur de réinitialiser les mots de passe utilisateur"
      },
      "status": true
    }
  ]
}
```

---

#### 4. Assign Permission to User
**Endpoint:** `POST /api/admin/users/{userId}/assign-droit/{droitId}`

**Headers:**
- Authorization: Bearer {token}
- Content-Type: application/json

**Response (200 OK):**
```json
{
  "message": "Droit assigné à l'utilisateur avec succès",
  "userId": 20,
  "droitId": 1,
  "droitCode": "USER_ENABLE_DISABLE",
  "droitLibelle": "Activer ou désactiver le compte utilisateur"
}
```

---

#### 5. Remove Permission from User
**Endpoint:** `DELETE /api/admin/users/{userId}/remove-droit/{droitId}`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "message": "Droit supprimé de l'utilisateur avec succès",
  "userId": 20,
  "droitId": 1
}
```

---

#### 6. Set User Status (Enable/Disable)
**Endpoint:** `POST /api/admin/users/{userId}/status`

**Headers:**
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body:**
```json
{
  "status": true
}
```

**Response (200 OK):**
```json
{
  "message": "Statut de l'utilisateur mis à jour avec succès",
  "userId": 19,
  "status": true
}
```

---

#### 7. Reset User Password to Default
**Endpoint:** `POST /api/admin/users/{userId}/reset-password`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "message": "Mot de passe de l'utilisateur réinitialisé par défaut avec succès"
}
```

**Note:** Password is reset to `Admin@123` (default for all users)

---

#### 8. Assign Role to User
**Endpoint:** `POST /api/admin/users/{userId}/assign-role/{roleId}`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "message": "Rôle assigné à l'utilisateur avec succès",
  "userId": 20,
  "roleId": 1,
  "roleName": "Administrator"
}
```

---

#### 9. Remove Role from User
**Endpoint:** `POST /api/admin/users/{userId}/remove-role`

**Headers:**
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body:**
```json
{
  "roleId": 1
}
```

**Response (200 OK):**
```json
{
  "message": "Rôle supprimé de l'utilisateur avec succès",
  "userId": 20,
  "roleId": 1,
  "roleName": "ROLE_ADMIN"
}
```

---

### ROLE MANAGEMENT

#### 10. Get All Roles
**Endpoint:** `GET /api/admin/roles`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "roles": [
    {
      "id": 1,
      "code": "ROLE_ADMIN",
      "libelle": "Administrateur",
      "description": "Accès complet au système avec toutes les permissions",
      "droits": [
        {
          "id": 1,
          "code": "USER_ENABLE_DISABLE",
          "libelle": "Activer ou désactiver le compte utilisateur",
          "description": "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs"
        }
      ]
    },
    {
      "id": 2,
      "code": "ROLE_NORMAL_USER",
      "libelle": "Utilisateur Normal",
      "description": "Utilisateur régulier avec des permissions limitées",
      "droits": []
    },
    {
      "id": 3,
      "code": "ROLE_CLIENT",
      "libelle": "Client",
      "description": "Client avec des permissions minimales",
      "droits": []
    }
  ]
}
```

---

#### 11. Get Single Role
**Endpoint:** `GET /api/admin/roles/{roleId}`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "id": 1,
  "code": "ROLE_ADMIN",
  "libelle": "Administrateur",
  "description": "Accès complet au système avec toutes les permissions",
  "droits": [
    {
      "id": 1,
      "code": "USER_ENABLE_DISABLE",
      "libelle": "Activer ou désactiver le compte utilisateur",
      "description": "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs"
    }
  ]
}
```

---

#### 12. Create Role
**Endpoint:** `POST /api/admin/roles`

**Headers:**
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body:**
```json
{
  "code": "ROLE_MANAGER",
  "libelle": "Gestionnaire",
  "description": "Gestionnaire avec des permissions spécifiques"
}
```

**Response (200 OK):**
```json
{
  "message": "Rôle créé avec succès",
  "role": {
    "id": 4,
    "code": "ROLE_MANAGER",
    "libelle": "Gestionnaire",
    "description": "Gestionnaire avec des permissions spécifiques",
    "droits": []
  }
}
```

---

#### 13. Assign Permission to Role
**Endpoint:** `POST /api/admin/roles/{roleId}/assign-droit/{droitId}`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "message": "Droit assigné au rôle avec succès",
  "roleId": 2,
  "droitId": 1
}
```

---

### PERMISSION MANAGEMENT

#### 14. Get All Permissions
**Endpoint:** `GET /api/admin/droits`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "USER_ENABLE_DISABLE",
    "libelle": "Activer ou désactiver le compte utilisateur",
    "description": "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs"
  },
  {
    "id": 2,
    "code": "USER_PASSWORD_RESET",
    "libelle": "Réinitialiser le mot de passe utilisateur",
    "description": "Permettre à l'administrateur de réinitialiser les mots de passe utilisateur"
  },
  {
    "id": 3,
    "code": "USER_ASSIGN_ROLE",
    "libelle": "Assigner un rôle à l'utilisateur",
    "description": "Permettre à l'administrateur d'assigner des rôles aux utilisateurs"
  },
  {
    "id": 4,
    "code": "USER_REMOVE_ROLE",
    "libelle": "Supprimer le rôle de l'utilisateur",
    "description": "Permettre à l'administrateur de supprimer des rôles des utilisateurs"
  },
  {
    "id": 5,
    "code": "USER_ASSIGN_DROIT",
    "libelle": "Assigner une permission à l'utilisateur",
    "description": "Permettre à l'administrateur d'assigner des permissions aux utilisateurs"
  },
  {
    "id": 6,
    "code": "USER_REMOVE_DROIT",
    "libelle": "Supprimer la permission de l'utilisateur",
    "description": "Permettre à l'administrateur de supprimer des permissions des utilisateurs"
  },
  {
    "id": 7,
    "code": "USER_CREATE",
    "libelle": "Créer un utilisateur",
    "description": "Permettre à l'administrateur de créer de nouveaux utilisateurs"
  },
  {
    "id": 8,
    "code": "USER_READ",
    "libelle": "Lire les informations de l'utilisateur",
    "description": "Permettre à l'utilisateur de voir les informations de l'utilisateur"
  },
  {
    "id": 9,
    "code": "USER_UPDATE",
    "libelle": "Mettre à jour les informations de l'utilisateur",
    "description": "Permettre à l'administrateur de mettre à jour les informations de l'utilisateur"
  },
  {
    "id": 10,
    "code": "USER_DELETE",
    "libelle": "Supprimer l'utilisateur",
    "description": "Permettre à l'administrateur de supprimer les utilisateurs"
  },
  {
    "id": 11,
    "code": "ROLE_CREATE",
    "libelle": "Créer un rôle",
    "description": "Permettre à l'administrateur de créer de nouveaux rôles"
  },
  {
    "id": 12,
    "code": "ROLE_READ",
    "libelle": "Lire les informations du rôle",
    "description": "Permettre de voir les informations du rôle"
  },
  {
    "id": 13,
    "code": "ROLE_UPDATE",
    "libelle": "Mettre à jour le rôle",
    "description": "Permettre à l'administrateur de mettre à jour le rôle"
  },
  {
    "id": 14,
    "code": "ROLE_DELETE",
    "libelle": "Supprimer le rôle",
    "description": "Permettre à l'administrateur de supprimer le rôle"
  },
  {
    "id": 15,
    "code": "ROLE_ASSIGN_DROIT",
    "libelle": "Assigner un droit au rôle",
    "description": "Permettre à l'administrateur d'assigner des permissions aux rôles"
  },
  {
    "id": 16,
    "code": "DROIT_CREATE",
    "libelle": "Créer une permission",
    "description": "Permettre à l'administrateur de créer de nouvelles permissions"
  },
  {
    "id": 17,
    "code": "DROIT_READ",
    "libelle": "Lire la permission",
    "description": "Permettre de voir les permissions"
  },
  {
    "id": 18,
    "code": "DROIT_UPDATE",
    "libelle": "Mettre à jour la permission",
    "description": "Permettre à l'administrateur de mettre à jour la permission"
  },
  {
    "id": 19,
    "code": "DROIT_DELETE",
    "libelle": "Supprimer la permission",
    "description": "Permettre à l'administrateur de supprimer la permission"
  },
  {
    "id": 20,
    "code": "ADMIN_ACCESS",
    "libelle": "Accès administrateur",
    "description": "Permettre un accès complet au panneau d'administration"
  },
  {
    "id": 21,
    "code": "VIEW_DASHBOARD",
    "libelle": "Afficher le tableau de bord",
    "description": "Permettre à l'utilisateur d'afficher le tableau de bord"
  },
  {
    "id": 22,
    "code": "VIEW_REPORTS",
    "libelle": "Afficher les rapports",
    "description": "Permettre à l'utilisateur d'afficher les rapports"
  }
]
```

---

#### 15. Get Single Permission
**Endpoint:** `GET /api/admin/droits/{droitId}`

**Headers:**
- Authorization: Bearer {token}

**Response (200 OK):**
```json
{
  "id": 1,
  "code": "USER_ENABLE_DISABLE",
  "libelle": "Activer ou désactiver le compte utilisateur",
  "description": "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs"
}
```

---

#### 16. Create Permission
**Endpoint:** `POST /api/admin/droits`

**Headers:**
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body:**
```json
{
  "code": "CUSTOM_PERMISSION",
  "libelle": "Permission personnalisée",
  "description": "Une permission personnalisée pour des opérations spécifiques"
}
```

**Response (200 OK):**
```json
{
  "message": "Droit créé avec succès",
  "droit": {
    "id": 23,
    "code": "CUSTOM_PERMISSION",
    "libelle": "Permission personnalisée",
    "description": "Une permission personnalisée pour des opérations spécifiques"
  }
}
```

---

## Error Responses

All endpoints (except login/register) return these errors when not authenticated or user not found:

**401 Unauthorized:**
```json
{
  "message": "Unauthorized"
}
```

**400 Bad Request (User Not Found):**
```json
{
  "message": "Utilisateur non trouvé"
}
```

**400 Bad Request (Role Not Found):**
```json
{
  "message": "Rôle non trouvé"
}
```

**400 Bad Request (Permission Not Found):**
```json
{
  "message": "Droit non trouvé"
}
```

**400 Bad Request (Missing Code/Libelle):**
```json
{
  "message": "Code et libelle sont requis"
}
```

---

## Test Users

### Admin User
- **Login:** admin
- **Password:** Admin@123
- **Role:** ROLE_ADMIN (22 permissions)

### Normal User
- **Login:** USER
- **Password:** Admin@123
- **Role:** ROLE_NORMAL_USER (5 permissions)

---

## Using Postman

1. **Login first** to get a token:
   - POST `/api/auth/login`
   - Body: `{"login":"admin","pwd":"Admin@123"}`
   - Copy the `token` from response

2. **Set Authorization for admin endpoints:**
   - Tab: Authorization
   - Type: Bearer Token
   - Token: Paste the copied token

3. **Send requests** with appropriate body where required

---

## Summary

- **Total Endpoints:** 17 (3 auth + 14 admin)
- **Auth Methods:** POST, GET
- **Admin Methods:** GET, POST, DELETE
- **Default Password Reset:** Admin@123
- **Token Expiration:** 24 hours
- **CORS:** Enabled for all origins (*)
- **Authentication:** JWT (Bearer Token)
- **API Language:** French (All response messages and descriptions in French)
- **Last Updated:** March 2, 2026
