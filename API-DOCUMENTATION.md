# Teachy API Documentation

This document provides detailed information about the RESTful API endpoints available in the Teachy Online Learning Platform.

## Authentication Endpoints

### Login

**Endpoint:** `GET /login`

**Description:** Displays the login page for users to authenticate.

**Response:** Returns the login page HTML.

**Example:**
```
GET /login
```

---

### Login Processing

**Endpoint:** `POST /login`

**Description:** Processes the login form submission.

**Request Body:**
- `username` (string, required): The user's username
- `password` (string, required): The user's password

**Response:**
- Success: Redirects to the appropriate home page based on user role
- Failure: Redirects to `/login?error`

**Example:**
```
POST /login
Content-Type: application/x-www-form-urlencoded

username=johndoe&password=secretpassword
```

---

### Registration Page

**Endpoint:** `GET /register`

**Description:** Displays the registration page for new users.

**Response:** Returns the registration page HTML.

**Example:**
```
GET /register
```

---

### Process Registration

**Endpoint:** `POST /register`

**Description:** Processes the registration form submission.

**Request Body:**
- `username` (string, required): The desired username
- `email` (string, required): The user's email address
- `password` (string, required): The desired password
- `role` (string, required): The user role (STUDENT, TEACHER, or ADMIN)

**Response:**
- Success: Redirects to `/login?registered`
- Failure: Returns error message

**Example:**
```
POST /register
Content-Type: application/x-www-form-urlencoded

username=johndoe&email=john@example.com&password=secretpassword&role=STUDENT
```

---

### Logout

**Endpoint:** `GET /logout`

**Description:** Logs out the current user.

**Response:** Redirects to `/login?logout`

**Example:**
```
GET /logout
```

## Home Endpoints

### Home Page

**Endpoint:** `GET /`

**Description:** Displays the main home page of the application.

**Response:** Returns the home page HTML.

**Example:**
```
GET /
```

---

### Home Redirect

**Endpoint:** `GET /home/redirect`

**Description:** Redirects users to their role-specific home page.

**Response:** Redirects to one of:
- `/admin/home` for administrators
- `/teacher/home` for teachers
- `/student/home` for students
- `/login?error` if role cannot be determined

**Example:**
```
GET /home/redirect
```

## Role-Specific Endpoints

### Student Home

**Endpoint:** `GET /student/home`

**Description:** Displays the student dashboard.

**Authorization:** Requires STUDENT role.

**Response:** Returns the student home page HTML.

**Example:**
```
GET /student/home
```

---

### Teacher Home

**Endpoint:** `GET /teacher/home`

**Description:** Displays the teacher dashboard.

**Authorization:** Requires TEACHER role.

**Response:** Returns the teacher home page HTML.

**Example:**
```
GET /teacher/home
```

---

### Admin Home

**Endpoint:** `GET /admin/home`

**Description:** Displays the administrator dashboard.

**Authorization:** Requires ADMIN role.

**Response:** Returns the admin home page HTML.

**Example:**
```
GET /admin/home
```

## Error Handling

All API endpoints follow a consistent error handling pattern:

- **400 Bad Request**: Invalid input parameters
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected server error

Error responses include a descriptive message to help troubleshoot the issue.

## Authentication

The API uses session-based authentication with Spring Security. After successful login, a session cookie (JSESSIONID) is set and must be included in subsequent requests.

## Content Types

- Request: `application/x-www-form-urlencoded` or `application/json`
- Response: `text/html` for page views, `application/json` for data responses

---

*Last updated: July 31, 2025*