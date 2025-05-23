Project Overview

● Purpose: A digital wallet platform allowing users to register, deposit, withdraw, transfer
funds, and view transaction history, with admin features for monitoring and reporting.

● Tech Stack:

○ Backend: Spring Boot 3.3.5, Java 17

○ Database: MySQL (digital_wallet schema)

○ ORM: Spring Data JPA, MyBatis (for specific queries)

○ Security: Spring Security with UUID-based Bearer token authentication

○ API Documentation: Swagger (Springfox 3.0.0)

○ Dependencies: Spring Web, JPA, Security, MySQL Connector, Lombok, MyBatis

● Authentication: Uses a single UUID token (e.g.,21edf3f6-23a9-4692-8f6b-708485636ad3) stored in the tokens table, validated against
the database for each request, replacing JWT.
