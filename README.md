# ProCly: Order Management System 

## Project Overview

This project is a desktop application designed to manage orders and invoices. It allows users to keep track of their products, clients, sales invoices, and internal product requests (commands/orders). The application is built using Java and a MySQL database for data persistence.



## Key Features

- **Product Management:** Add, modify, delete, and view products (name, price, quantity).
- **Client Management:** Add, modify, delete, and view clients (name, email, phone).
- **Invoice Management:** Generate invoices, associating them with clients and products with their respective quantities, including subtotal and total calculations.
- **Command/Order Management:** Create internal product requests (commands), associating them with clients and products, tracking quantities, and calculating total costs.
- **Data Persistence:** All data is stored in a MySQL database.

## Technologies Used

- **Programming Language:** Java
- **Database:** MySQL

## Project Structure

The project is organized using a layered architecture:

```
src
└── main
    ├── java
    │   └── com
    │       └── example
    │           ├── Main.java
    │           ├── dao
    │           │   ├── ClientDAO.java
    │           │   ├── CommandDAO.java
    │           │   ├── InvoiceDAO.java
    │           │   └── ProductDAO.java
    │           ├── database
    │           │   └── DatabaseConnection.java
    │           ├── models
    │           │   ├── Client.java
    │           │   ├── Command.java
    │           │   ├── Invoice.java
    │           │   ├── LineItem.java
    │           │   └── Product.java
    │           ├── services
    │           │   ├── ClientService.java
    │           │   ├── CommandService.java
    │           │   ├── InvoiceService.java
    │           │   └── ProductService.java
    │           ├── ui
    │           │   ├── ClientManagementUI.java
    │           │   ├── CommandManagementUI.java
    │           │   ├── InvoiceManagementUI.java
    │           │   └── ProductManagementUI.java
    │           └── utils
    │               ├── CSVExporter.java
    │               └── PDFExporter.java
    └── resources
        └── style.css
```

- **`database`:** Contains the `DatabaseConnection.java` file, responsible for establishing a connection to the MySQL database.
- **`models`:** Data model classes representing entities in the database (e.g., `Product`, `Client`, `Invoice`, `Command`, `LineItem`).
- **`dao`:** Data Access Object classes responsible for interacting with the database (e.g., `ProductDAO`, `ClientDAO`, `InvoiceDAO`, `CommandDAO`).
- **`services`:** Classes containing the business logic of the application, acting as an intermediary between the UI and DAOs (e.g., `ProductService`, `ClientService`, `InvoiceService`, `CommandService`).
- **`ui`:** This folder will hold the JavaFX user interface related code.
- **`Main`:** The `Main` class that will bootstrap the JavaFX application.

## Setting Up the Project

Follow these steps to set up the project locally:

### 1. Prerequisites

- **Java Development Kit (JDK):** Make sure you have Java 11 or higher installed on your system. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/downloads/).
- **Java FX:** Make sure to add it [Java FX's website](https://openjfx.io/).
- **MySQL Database:** You need to have MySQL server installed and running. You can download it from the [MySQL website](https://dev.mysql.com/downloads/mysql/).

### 2. Clone the Repository

Clone the project repository to your local machine using Git:

```bash
git clone https://github.com/oddmaw/procly.git
```

### 3. Database Setup

1. **Create Database:** Create a database named `procly_db` in your MySQL server:

    ```sql
    CREATE DATABASE IF NOT EXISTS company_db;
    USE company_db;
    ```

2. **Create Tables:** Execute the provided SQL script to create tables:

    ```sql
    -- Create Database
    CREATE DATABASE IF NOT EXISTS procly_db;

    -- Use Database
    USE procly_db;

    -- Create Products Table
    CREATE TABLE IF NOT EXISTS products (
        idProduit INT AUTO_INCREMENT PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        prix DOUBLE NOT NULL,
        quantiteEnStock INT NOT NULL
    );

    -- Create Clients Table
    CREATE TABLE IF NOT EXISTS clients (
        idClient INT AUTO_INCREMENT PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        email VARCHAR(255),
        telephone VARCHAR(20)
    );

    -- Create Factures Table
    CREATE TABLE IF NOT EXISTS factures (
        idFacture INT AUTO_INCREMENT PRIMARY KEY,
        date DATETIME DEFAULT CURRENT_TIMESTAMP,
        montantTotal DOUBLE NOT NULL,
        idClient INT NULL,
        FOREIGN KEY (idClient) REFERENCES clients(idClient) ON DELETE SET NULL
    );

    -- Create Lignes_Facture Table
    CREATE TABLE IF NOT EXISTS lignes_facture (
        idLigne INT AUTO_INCREMENT PRIMARY KEY,
        idFacture INT NOT NULL,
        idProduit INT NULL,
        quantite INT NOT NULL,
        sousTotal DOUBLE NOT NULL,
        FOREIGN KEY (idFacture) REFERENCES factures(idFacture),
        FOREIGN KEY (idProduit) REFERENCES products(idProduit) ON DELETE SET NULL
    );

    -- Create Commandes Table
    CREATE TABLE IF NOT EXISTS commandes (
        idCommande INT AUTO_INCREMENT PRIMARY KEY,
        date DATETIME DEFAULT CURRENT_TIMESTAMP,
        idClient INT NULL,
        FOREIGN KEY (idClient) REFERENCES clients(idClient) ON DELETE SET NULL
    );

    -- Create Lignes_Commande Table
    CREATE TABLE IF NOT EXISTS lignes_commande (
        idLigneCommande INT AUTO_INCREMENT PRIMARY KEY,
        idCommande INT NOT NULL,
        idProduit INT NULL,
        quantite INT NOT NULL,
        sousTotal DOUBLE NOT NULL,
        FOREIGN KEY (idCommande) REFERENCES commandes(idCommande),
        FOREIGN KEY (idProduit) REFERENCES products(idProduit) ON DELETE SET NULL
    );

    -- Add totalDiscount column
    ALTER TABLE factures
    ADD COLUMN totalDiscount DOUBLE DEFAULT 0.0;
    ```

### 4. Configure the Database Connection

1. **Open `src/database/DatabaseConnection.java`:**
2. **Update the Connection Details:** Modify the following constants with your MySQL server details:

    ```java
    private static final String DB_URL = "jdbc:mysql://localhost:port/procly_db";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";
    ```

## Contribution

Feel free to contribute to this project by creating pull requests. Follow good coding standards and test your code before submitting changes.

## Contact

If you have any questions or issues, please feel free to reach out.
