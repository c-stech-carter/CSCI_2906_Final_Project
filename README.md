(This repository contains my coursework for CSCI 2906- Final Project at Southwest Tech)

# Library Management System

## Overview
This **Library Management System** is a JavaFX-based desktop application designed to allow an independent library to have a simple collection management system, including circulation.

## Features
- **Master Control Panel** - Provides a central interface to access different modules.
- **Circulation System** - Allows users to check out and return books.
- **Book Browser** - Enables users to search for and view books.
- **Cataloging System** - Allows administrators to add and remove books.
- **User Registration** - Manages user sign-ups and removals.


## Getting Started
### Prerequisites
Ensure you have the following installed:
- **Java 17 or later**
- **JavaFX SDK**
- (Optional) **Maven or Gradle** for dependency management

### Running the Application
#### Using an IDE
1. Import the project into **IntelliJ IDEA** or **Eclipse**.
2. Ensure JavaFX libraries are properly configured.
3. Run `MasterApp.java` to start the system.

#### Using Command Line
1. Navigate to the project directory.
2. Compile the code:
   ```sh
   javac -d out --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml src/carter/stech/librarysystemv2/*.java
   ```
3. Run the application:
   ```sh
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp out carter.stech.librarysystemv2.MasterApp
   ```

## Modules Description
### 1. **MasterApp.java**
This is the main entry point that allows navigation between different modules.

### 2. **Book.java**
Defines the book model, including details like title, author, ISBN/BookID, availability, and borrower information.

### 3. **User.java**
Represents library users, including user ID, name, and a list of checked-out books.

### 4. **CirculationApp.java**
Handles book checkouts and check-ins.   Automatically adds a 2 week borrowing period.

### 5. **BookBrowserApp.java**
Provides a searchable interface to browse available books.   May search by Author, Title, or BookID.  Searches can be partial entries, as well.

### 6. **CatalogingApp.java**
Allows library administrators to add and remove books from the collection.  Books cannot be removed if they are currently checked out.

### 7. **UserRegistrationApp.java**
Manages user registration and deletion.  Does not allow a user to be deleted if they have active checkouts.

### 8. **Main.java**
Provides an entry point for building the application as a runnable JAR.

## Data Storage
The application stores book and user information in JSON format:
- `books.json` - Contains all book data.
- `users.json` - Stores user details and checked-out books.

## Future Improvements
- Enhance the **GUI design** with more advanced JavaFX styling.



## Author
**Charles Carter

---
