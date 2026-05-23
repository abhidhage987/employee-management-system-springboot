# Employee Management System - Spring Boot

## 🚀 Project Overview

Enterprise-level Employee Management System built using Java Spring Boot, PostgreSQL, Spring Security, and JWT Authentication.

This project demonstrates real-world backend development concepts used in modern enterprise applications including authentication, authorization, REST APIs, exception handling, pagination, logging, and Swagger API documentation.

---

# 🛠️ Technologies Used

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* PostgreSQL
* Spring Data JPA
* Hibernate
* Maven
* Swagger OpenAPI
* REST APIs
* Git & GitHub

---

# ✨ Features

## Employee Management

* Add Employee
* Get All Employees
* Get Employee By ID
* Update Employee
* Delete Employee
* Search Employee By Department
* Pagination & Sorting

---

## Security Features

* JWT Authentication
* Login & Register APIs
* Refresh Token Mechanism
* Logout Mechanism
* Token Expiry Handling
* Role-Based Access Control
* Protected APIs
* Password Encryption using BCrypt

---

## Exception Handling

* Global Exception Handling
* Custom Exceptions
* Validation Handling
* Clean API Responses

---

## API Documentation

* Swagger UI Integration

---

# 📁 Project Structure

```text
com.abhi.employeemanagement
│
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
├── util
└── EmployeeManagementSystemApplication
```

---

# 🔐 Authentication Flow

```text
Register User
    ↓
Login User
    ↓
Generate Access Token + Refresh Token
    ↓
Access Protected APIs
    ↓
Refresh Expired Token
    ↓
Logout User
```

---

# 🗄️ Database Configuration

## PostgreSQL

Create Database:

```sql
CREATE DATABASE employee_db;
```

---

## application.properties

```properties
spring.application.name=EmployeeManagementSystem

# DATABASE
spring.datasource.url=jdbc:postgresql://localhost:5432/employee_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# SERVER
server.port=8080
```

---

# ▶️ Run Project

## Clone Repository

```bash
git clone https://github.com/abhidhage987/employee-management-system-springboot.git
```

---

## Open Project

Import project into:

* Spring Tool Suite (STS)
  OR
* IntelliJ IDEA

---

## Run Application

```bash
mvn spring-boot:run
```

OR run:
`EmployeeManagementSystemApplication.java`

---

# 📚 Swagger API Documentation

Open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 🔑 Authentication APIs

## Register User

### POST

```text
/api/auth/register
```

### Request Body

```json
{
  "name": "Abhishek",
  "email": "abhi@gmail.com",
  "password": "12345"
}
```

---

## Login User

### POST

```text
/api/auth/login
```

### Request Body

```json
{
  "email": "abhi@gmail.com",
  "password": "12345"
}
```

### Response

```json
{
  "accessToken": "jwt_access_token",
  "refreshToken": "jwt_refresh_token"
}
```

---

## Refresh Token

### POST

```text
/api/auth/refresh
```

### Request Body

```json
{
  "refreshToken": "your_refresh_token"
}
```

---

## Logout

### POST

```text
/api/auth/logout
```

### Request Body

```json
{
  "refreshToken": "your_refresh_token"
}
```

---

# 👨‍💻 Employee APIs

## Add Employee

### POST

```text
/api/employees
```

---

## Get All Employees

### GET

```text
/api/employees
```

---

## Get Employee By ID

### GET

```text
/api/employees/{id}
```

---

## Update Employee

### PUT

```text
/api/employees/{id}
```

---

## Delete Employee

### DELETE

```text
/api/employees/{id}
```

---

# 🔎 Pagination API

### GET

```text
/api/employees/pagination?page=0&size=5&sortBy=name
```

---

# 🔍 Search API

### GET

```text
/api/employees/search?department=IT
```

---

# 🔒 Role-Based Access

| Role  | Access                     |
| ----- | -------------------------- |
| USER  | Employee APIs              |
| ADMIN | Employee APIs + Admin APIs |

---

# 📌 Important Concepts Implemented

* Dependency Injection
* IoC Container
* DTO Pattern
* Layered Architecture
* RESTful APIs
* JWT Security
* Stateless Authentication
* Validation
* Logging
* Exception Handling
* Pagination & Sorting

---

# 🚀 Future Enhancements

* Email Verification
* OTP Authentication
* File Upload
* Redis Cache
* Docker
* CI/CD
* Microservices
* Kafka / RabbitMQ
* API Gateway

---

# 👨‍💻 Author

Abhishek Dhage

GitHub:
https://github.com/abhidhage987

---

# ⭐ If You Like This Project

Give this repository a star ⭐ on GitHub.
