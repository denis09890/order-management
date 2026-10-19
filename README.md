# 📦 Warehouse Orders Management System

A multi-layered desktop application designed for processing client orders and managing warehouse inventory. The system is built in Java and connects to a relational database using JDBC, featuring dynamic table rendering via Java Reflection, generic DAO operations, and immutable order logging.

---

## 📑 Table of Contents
- [Architecture & Design](#-architecture--design)
- [Key Features](#-key-features)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Technical Highlights](#-technical-highlights)
- [Getting Started](#-getting-started)
- [Documentation & Diagrams](#-documentation--diagrams)

---

## 🏛 Architecture & Design

The project strictly follows the **Layered Architecture (N-Tier)** design pattern to ensure high cohesion, low coupling, and maintainability:

1. **`model` Layer**: Contains plain data objects (POJOs) mapping the database entities (`Client`, `Product`, `Order`) alongside immutable data structures.
2. **`dataAccessLayer` (DAO)**: Manages communication with the relational database. Implements a generic DAO pattern using Java Reflection to execute dynamic CRUD statements.
3. **`businessLayer` (BLL)**: Encapsulates core business rules, validation logic, stock checks, and transaction integrity.
4. **`presentation` Layer**: Built with Java Swing to provide an intuitive GUI for warehouse managers and operators.

---

## ✨ Key Features

### 👤 Client Management
- Full CRUD operations: Create, Read/View, Update, and Delete clients.
- Tabular display using dynamic `JTable` rendering.

### 📦 Product & Inventory Management
- Real-time stock tracking and price management.
- Dynamic data listing and modification of warehouse stock.

### 🛒 Order Processing & Stock Automation
- Integrated order placement interface: select client, select product, and define desired quantity.
- **Stock Validation**: Automatically checks inventory before finalizing orders; alerts the user with an **under-stock message** if requested quantity exceeds available inventory.
- **Automatic Stock Decrement**: Automatically decreases stock levels upon order completion.
- **Immutable Billing Log**: Automatically creates an immutable `Bill` record and registers it into a database `Log` table (insert and read operations only).

---

## ⚙️ Technical Highlights

* **Java Reflection (Dynamic GUI Generation)**:
  - Tables (`JTable`) are dynamically populated by extracting object fields via runtime reflection. The table headers and rows are derived dynamically from model attributes.
* **Generic Data Access Object (`AbstractDAO<T>`)**:
  - Leverages Java Reflection and Generics to generate runtime `INSERT`, `UPDATE`, `DELETE`, and `SELECT` SQL queries for any model class without boilerplate duplication.
* **Java Records**:
  - The `Bill` object is modeled as a canonical, immutable Java `record` to ensure financial logs cannot be tampered with or modified.
* **Functional Programming**:
  - Utilizes **Java Streams API** and lambda expressions for collection filtering, entity transformations, and in-memory queries.
* **Clean Code & OOP Constraints**:
  - Adherence to Java naming conventions, strict method length limits ($\le 30$ lines), and class size limits ($\le 300$ lines for business/model components).

---

## 🗄 Database Schema

The database requires at least the following relational tables:
* `Client` (`id`, `name`, `email`, `address`, ...)
* `Product` (`id`, `name`, `stock`, `price`, ...)
* `Order` (`id`, `client_id`, `product_id`, `quantity`, `order_date`, ...)
* `Log` (`id`, `order_id`, `client_name`, `product_name`, `quantity`, `total_price`, `timestamp`) — *Append-only log for immutable `Bill` records.*

> A full database dump containing the creation statements and sample data is included in `database_dump.sql`.

---

## 📂 Project Structure

```text
src/
├── businessLayer/
│   ├── ClientBLL.java
│   ├── ProductBLL.java
│   └── OrderBLL.java
├── dataAccessLayer/
│   ├── AbstractDAO.java       # Generic DAO using Java Reflection
│   ├── ClientDAO.java
│   ├── ProductDAO.java
│   ├── OrderDAO.java
│   └── BillDAO.java           # Append-only / Read-only log DAO
├── model/
│   ├── Client.java
│   ├── Product.java
│   ├── Order.java
│   └── Bill.java              # Java Record (Immutable)
├── presentation/
│   ├── MainView.java
│   ├── ClientView.java
│   ├── ProductView.java
│   ├── OrderView.java
│   ├── ReflectionTable.java   # Dynamic JTable generator via Reflection
│   └── Controller.java
└── connection/
    └── ConnectionFactory.java # JDBC connection manager
