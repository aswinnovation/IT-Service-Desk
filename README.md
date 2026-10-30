# IT Service Desk Ticket Management System

A beginner-friendly IT support ticket management application built with Java, Spring Boot, MySQL, HTML, CSS and JavaScript.

## Features

- Create and view IT support tickets
- Track ticket status: Open, In Progress, Resolved and Closed
- Set priority: Low, Medium, High and Critical
- Categorize requests: Hardware, Software, Network, Access and Other
- Search tickets and filter by status or priority
- Dashboard ticket counts
- Export ticket records as CSV

## Technology Stack

- Backend: Java 21, Spring Boot 3.5.6, Spring Data JPA
- Database: MySQL
- Frontend: HTML5, CSS3, JavaScript
- Build Tool: Maven

## Database Setup

1. Start MySQL Server.
2. Create the database by running `database/schema.sql` in MySQL Workbench.
3. Open `src/main/resources/application.properties`.
4. Set the correct MySQL username and password.

Example configuration:

```properties
spring.application.name=it-service-desk
spring.datasource.url=jdbc:mysql://localhost:3306/it_service_desk?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

Do not commit your real database password to a public repository.

## Run the Application

From the project root, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Open the application at:

`http://localhost:8080`

## REST API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tickets` | List all tickets |
| GET | `/api/tickets/{id}` | Get one ticket |
| POST | `/api/tickets` | Create a ticket |
| PUT | `/api/tickets/{id}` | Update ticket details |
| PATCH | `/api/tickets/{id}/status` | Update ticket status |
| DELETE | `/api/tickets/{id}` | Delete a ticket |
| GET | `/api/tickets/export` | Download CSV report |

## Project Structure

```text
src/main/java/com/example/IT_Service_Desk/
├── controller/
├── entity/
├── repository/
├── service/
└── ItServiceDeskApplication.java

src/main/resources/
├── static/
│   ├── css/
│   ├── js/
│   └── index.html
└── application.properties

database/
└── schema.sql
```
