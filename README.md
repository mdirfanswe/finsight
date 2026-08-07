# 💰 FinSight

FinSight is a full-stack personal finance management application that helps users track, manage, and analyze their income and expenses through an interactive dashboard.

The application provides secure authentication, transaction management, filtering, pagination, financial summaries, and data visualization.

## 🚀 Features

- 🔐 User authentication with Spring Security and JWT
- 📧 Email-based account verification and password reset
- 💰 Income management with full CRUD operations
- 💸 Expense management with full CRUD operations
- 📊 Interactive financial dashboards and charts
- 🔎 Transaction filtering by date, month, year, and recent days
- 📄 Pagination for transaction records
- 📈 Income, expense, and financial summary analysis
- 🗑️ Secure transaction deletion and updates
- 🐳 Docker and Docker Compose support
- ☁️ MySQL database hosted using Aiven Cloud

## 🧠 Tech Stack

### Frontend
- React.js
- Vite
- JavaScript
- Recharts
- CSS

### Backend
- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- REST APIs

### Database
- MySQL
- Aiven Cloud

### DevOps / Tools
- Docker
- Docker Compose
- Git
- GitHub
- Postman

## 🧩 Architecture

FinSight follows a client-server architecture:

```text
React Frontend
      │
      │ REST API
      ▼
Spring Boot Backend
      │
      │ JPA / Hibernate
      ▼
MySQL Database