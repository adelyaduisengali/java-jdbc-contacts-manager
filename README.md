# Contacts Manager – Java + PostgreSQL (JDBC)

A console application in Java for managing contacts stored in a PostgreSQL
database. Implements full CRUD operations using JDBC.

## Features
- Create, view, update and delete contacts through a text menu
- Search a contact by id
- Each contact has first name, last name, phone, email (optional),
  type (friend / family / work) and creation date
- Input validation (required fields, numeric id, unique phone number)
- Prepared statements to protect against SQL injection
- Error handling for database and input errors

## Tech stack
Java · JDBC · PostgreSQL · IntelliJ IDEA

## Database setup
Create the table in PostgreSQL:

    CREATE TABLE contacts (
        id SERIAL PRIMARY KEY,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        phone VARCHAR(20) UNIQUE NOT NULL,
        email VARCHAR(100),
        contact_type VARCHAR(20) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

## How to run
1. Add the PostgreSQL JDBC driver to the project
2. Set your database URL, username and password in `src/Main.java`
3. Run `Main.java` and choose an option from the menu (1–6)
