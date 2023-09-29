# RESTful User Management API

>This project is a practical test assignment focusing on the development of a RESTful API for user management using the Spring Boot framework. The API allows you to perform various operations on user resources, including user creation, updating user information, deleting users, and searching for users by birth date range.

## Table of Contents

- [Implementation Details](#hammer-implementation-details)
    - [User Resource Fields](#user-resource-fields)
    - [Functionality](#functionality)
    - [Unit Tests and Error Handling](#unit-tests-and-error-handling)
    - [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)


## :hammer: Implementation Details

### User Resource Fields

The user resource has the following fields:

1. **Email** - Validated against an email pattern.
2. **First name**.
3. **Last name**.
4. **Birthdate** - The value must be earlier than the current date.
5. **Address**.
6. **Phone number**.

### Functionality

The API provides the following functionality:

2.1. **Create User**: Allows user registration. Users must be at least 18 years old to register. The minimum age requirement (18) is configurable in the properties file.

2.2. **Update User Fields**: Allows updating one or more user fields.

2.3. **Update All User Fields**: Allows updating all user fields.

2.4. **Delete User**: Permits the deletion of a user.

2.5. **Search Users by Birth Date Range**: Enables searching for users within a specified birth date range. The API validates that the "From" date is less than the "To" date and returns a list of matching user objects.

2.6. **Search Users by Params**: Allows searching for users based on specific parameters.

2.7. **Search User by Id**: Allows searching for a user by their unique identifier.


### Unit Tests and Error Handling

The codebase is thoroughly covered by unit tests using the Spring testing framework. Additionally, the code incorporates error handling mechanisms to ensure robustness and user-friendly responses for RESTful requests.

### Technology Stack

- Java 17
- Spring Boot 3.1.4
- Springdoc OpenAPI (Swagger) 2.1.0
- Liquibase
- MySQL
- Lombok
- Jjwt 0.9.1
- Apache Maven
- MapStruct
- Mockito
- MockMvc
- Testcontainers

## Project Structure

> The project has a Three-Tier Architecture:

| Layer                                 | Responsibilities                                                              | 
|---------------------------------------|-------------------------------------------------------------------------------|
| **Presentation layer (Controllers)**  | Accepts requests from clients and sends results back to them.                 |
| **Application logic layer (Service)** | Provide logic to operate on the data sent to and from the DAO and the client. |
| **Data access layer (Repository)**    | Represents a bridge between the database and the application.                 |

## Getting Started

To get started with the test task solution, follow the instructions below:

### Local Run

* Clone the repository.
* Set your credentials in `application.properties`.
* Set your credentials for tests in `application.properties`.
* run Docker (for test containers)
* Build the project: `mvn clean package`.
* Run the application.
* Use this postman [collection](https://www.postman.com/supply-observer-16858482/workspace/for-people/collection/27238121-54ceab24-1d1e-4ea6-a879-3a1a5fb524db?action=share&creator=27238121) or [swagger](http://localhost:8080/swagger-ui.html)

### Run with docker

* Clone the repository.
* run docker
* run with command `docker-compose up`
* Use this postman [collection](https://www.postman.com/supply-observer-16858482/workspace/for-people/collection/27238121-8b1b9413-31a3-4184-9600-d8837270417c?action=share&creator=27238121)

> You can test the application using Swagger by accessing [http://localhost:8080/swagger-ui/index.html#](http://localhost:8080/swagger-ui/index.html#).

> Additionally, you can use the provided Postman collection for local run or the Docker run. Just import the collection into your Postman and start testing the application.

> Make sure to configure the necessary properties in `application.properties` for tests.

> User can register/login/update own info, search other users. But only Admin can also find user by id or delete user

## API Endpoints

Here are some example API endpoints to get you started:

- `POST /api/auth/registration`: Create a new user.
- `POST /api/auth/login`: Authenticate user.
- `PUT /api/users/{userId}`: Update specific user fields.
- `PATCH /api/users/{userId}`: Update all user fields.
- `DELETE /api/users/{userId}`: Delete a user.
- `GET /api/users/search-by-date-between?from={fromDate}&to={toDate}`: Search for users within a birthdate range.
- `GET /api/users?size={value}&page{value}&sort={value},{ASC/DESC}`: Search for users with pagination.
- `GET /api/users/search?{paramasName}={params value}`: Search for users by pramas.
- `GET /api/users/{id}`: Search user by id.

All API responses are in JSON format.

## GOOD LUCK

Good luck with your assignment! If you have any questions, feel free to reach out.
