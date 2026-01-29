# Rev_PasswordManager

A secure, console-based **Password Manager** application developed in **Java** using **JDBC** and **MySQL**.  
The application enables users to safely store, manage, and retrieve account passwords using strong security mechanisms.

---

## Project Overview

Rev_PasswordManager is designed to securely manage passwords for multiple online accounts.  
It provides features such as user authentication, encrypted password storage, password categorization, OTP-based verification, security questions for recovery, and audit logging.

The application follows a clean **layered architecture**:
- Controller
- Service
- DAO
- Utility

---

## Core Features

### User Management
- User registration with master password
- Secure login with account lock after multiple failed attempts
- Update user profile details
- Change master password
- Password recovery using:
  - Security questions
  - OTP verification

---

### Password Management
- Add and manage password entries
- AES encryption for stored account passwords
- View passwords after master password re-verification
- Update and delete password entries
- Search passwords by account name
- Maintain password history on updates

---

### Category Management
- Create and manage password categories
- Assign passwords to categories
- Delete unused categories

---

## Security Implementation

- Master passwords and security answers are stored using **one-way hashing**
- Account passwords are encrypted using **AES**
- OTP verification for sensitive operations
- Account lock mechanism after repeated failed login attempts
- Audit logs for tracking critical user actions

---

## Password Generator

- Generate strong random passwords
- Customizable options:
  - Password length
  - Uppercase letters
  - Lowercase letters
  - Numbers
  - Special characters

---

## Application Architecture

```
Controller
   ↓
Service
   ↓
DAO
   ↓
Database (MySQL)
```

---

## Testing

- Unit testing using **JUnit 5**
- Service layer tested with **Mockito**
- Utility classes fully unit tested
- DAO layer validated against real database

---

## Technologies Used

- Java  
- JDBC  
- MySQL  
- Maven  
- JUnit 5  
- Mockito  
- SLF4J / Log4j  

---

## Database

- MySQL database: `password_manager`
- Normalized relational schema with proper relationships

### Tables Used
-  users
-  password_entries
-  password_categories
-  security_questions
-  user_security_answer
-  verification_codes
- account_status
- login_attempts
- password_history
- audit_logs

---

## How to Run the Project

### Prerequisites
- Java 17 or higher
- MySQL Server
- Maven
### Steps

1. Create database `password_manager`  
2. Run SQL scripts to create tables  
3. Update database credentials in:
   ```
   org.example.config.DBConnection
   ```
4. Build the project:
   ```
   mvn clean install
   ```
5. Run the application:
   ```
   org.example.Main
   ```

---

## Project Structure

```
src
 ├── main
 │   └── java
 │       ├── controller
 │       ├── service
 │       ├── dao
 │       ├── model
 │       ├── util
 │       └── config
 └── test
     └── java
         ├── dao
         ├── service
         └── util
```

---


## Logging

- Logging implemented using **SLF4J with Log4j**
- Application logs are written to console and log files

