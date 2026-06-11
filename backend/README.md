# Expense Management System - Backend

Spring Boot REST API for the Expense Management application.

## Features

- User management (registration, retrieval, updates)
- Category management
- Expense tracking with status management
- Expense splitting functionality
- Complex database queries for analytics
- RESTful API endpoints
- Comprehensive unit testing

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- MySQL 8.0+

## Installation

### 1. Setup MySQL Database

```bash
# Create database
mysql -u root -p
> CREATE DATABASE expense_db;
> USE expense_db;
```

### 2. Configure Application

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build Project

```bash
mvn clean install
```

## Running the Application

```bash
# Using Maven
mvn spring-boot:run

# Application runs on http://localhost:8080
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with detailed output
mvn test -X
```

## API Endpoints

### Users
- `POST /api/users/register` - Register new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users` - Get all users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Categories
- `POST /api/categories` - Create category
- `GET /api/categories/{id}` - Get category by ID
- `GET /api/categories/user/{userId}` - Get user's categories
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}?userId={userId}` - Delete category

### Expenses
- `POST /api/expenses` - Create expense
- `GET /api/expenses/{id}` - Get expense by ID
- `GET /api/expenses/user/{userId}` - Get user's expenses
- `GET /api/expenses/user/{userId}/total-approved` - Get total approved expenses
- `GET /api/expenses/user/{userId}/range?startDate={date}&endDate={date}` - Get expenses by date range
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}?userId={userId}` - Delete expense

### Expense Splits
- `POST /api/expense-splits` - Create expense split
- `GET /api/expense-splits/{id}` - Get split by ID
- `GET /api/expense-splits/expense/{expenseId}` - Get splits for expense
- `GET /api/expense-splits/user/{userId}` - Get user's splits
- `GET /api/expense-splits/user/{userId}/total` - Get total split amounts
- `GET /api/expense-splits/expense/{expenseId}/validate` - Validate split total
- `DELETE /api/expense-splits/{id}` - Delete split

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/expense/
│   │   │   ├── model/          # Entity classes
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic layer
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   └── ExpenseManagementApplication.java  # Main class
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/expense/
│           ├── service/         # Service tests
│           └── controller/      # Controller tests
├── pom.xml
└── README.md
```

## Database Schema

### Users
- id (PK)
- email (UNIQUE)
- name
- password
- created_at
- updated_at

### Categories
- id (PK)
- name
- description
- user_id (FK)
- created_at

### Expenses
- id (PK)
- description
- amount
- status (PENDING, APPROVED, PAID, REJECTED)
- user_id (FK)
- category_id (FK)
- expense_date
- created_at
- updated_at

### Expense Splits
- id (PK)
- expense_id (FK)
- user_id (FK)
- split_amount
- created_at

## Exception Handling

The API returns appropriate HTTP status codes:
- 200 OK - Successful request
- 201 CREATED - Resource created
- 204 NO CONTENT - Successful deletion
- 400 BAD REQUEST - Invalid input
- 404 NOT FOUND - Resource not found
- 409 CONFLICT - Duplicate email or other conflicts
- 500 INTERNAL SERVER ERROR - Server error

## Performance Considerations

- Custom JPA queries for efficient data retrieval
- Lazy loading for related entities
- Connection pooling configured
- Database indexing on foreign keys

## Troubleshooting

### Connection Issues
- Verify MySQL is running: `mysql -u root -p`
- Check application.properties credentials
- Ensure database exists: `SHOW DATABASES;`

### Build Fails
- Clear Maven cache: `mvn clean`
- Check Java version: `java -version`
- Verify all dependencies: `mvn dependency:tree`

## Deployment

```bash
# Build JAR
mvn package

# Run JAR
java -jar target/expense-management-1.0.0.jar
```

## License

Developed as part of CAIT Capstone Project
