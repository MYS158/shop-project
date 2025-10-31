# 🏪 Shop Project

A Java-based inventory management application that uses a **MySQL database** and a **Swing graphical interface** to manage product records. Users can **Add**, **Update**, **Delete**, and **Search** products efficiently.

---

## 🚀 Overview

**Tech Stack:**  
- **Language:** Java 17+  
- **GUI Framework:** Swing  
- **Database:** MySQL (via JDBC)  
- **Build Tool:** Maven  
- **Testing:** JUnit 5

**Purpose:** Provide a user-friendly, maintainable system to manage product information, combining database persistence with a graphical interface.

---

## 🖥️ Graphical Interface Specifications

### Main Screen Layout
```
-----------------------------------------------------------------------------------------------------------------
| Product Catalog                                                                                               |
| ------------------------------------------------------------------------------------------------------------- |
| | ID: | TextField |  Description: | TextField                |                                    | | Add | Update | Delete | Search |
| | Brand: | ComboBox |  Content: | TextField |  Price: | TextField |                               |                                  |
| | Category: (RadioButtons) | Abarrotes | Personal Hygiene | Fruits & Vegetables | Wines & Liquors | |                                |
| | Status: | Checkbox (Active) |                                                                      |                                  |
| | Date made: | DateField (JCalendar) |                                                                |                                  |
| | Expiration date: | DateField (JCalendar) |                                                        |                                  |
| ------------------------------------------------------------------------------------------------------------- |
| Search Section                                                                                                |
| ------------------------------------------------------------------------------------------------------------- |
| | Brand Filter: | ComboBox |  Search Text: | TextField | | Search | | Clear |                               |
| ------------------------------------------------------------------------------------------------------------- |
-----------------------------------------------------------------------------------------------------------------
```

### Additional GUI notes
- **Layout Suggestion:** Use `GridBagLayout` for flexible component alignment. Combine with sub-panels or `BoxLayout` for better organization.
- **Detailed GUI Description:** The document is `docs/ui_main.png`.

📸 *A visual layout preview can be added later under* `docs/ui_main.png`.

---

## 🗄️ Database Specifications

**Table:** `Product`

| Column          | Type           | Constraints                        |
|-----------------|----------------|------------------------------------|
| id              | INT            | PRIMARY KEY, AUTO_INCREMENT, UNIQUE |
| description     | VARCHAR(30)    | NOT NULL                           |
| brand           | VARCHAR(30)    | NOT NULL                           |
| content         | VARCHAR(30)    | NOT NULL                           |
| category        | VARCHAR(30)    | NOT NULL                           |
| price           | DECIMAL(10,2)  | NOT NULL                           |
| status          | VARCHAR(15)    | NOT NULL                           |
| dateMade        | DATE           | NOT NULL                           |
| expirationDate  | DATE           | NULL                               |

**Engine:** InnoDB  
**Charset:** UTF8MB4

---

## ✅ Validation Rules

- **ID:** Positive integer (1–9999), unique per product.  
- **Price:** Must be greater than 0.  
- **Dates:** `dateMade` must be earlier than `expirationDate`.  
- **Category:** Must belong to {Abarrotes, Personal Hygiene, Fruits & Vegetables, Wines & Liquors}.  
- **Status:** Checked indicates *Active*.

---

## 📂 Project Structure

```
shop-project/
├── pom.xml                         # Maven configuration
├── README.md                       # Documentation (this file)
├── sql/
│   ├── schema.sql                  # CREATE TABLE scripts
│   └── seed.sql                    # Optional test data
├── config/
│   └── db.properties               # DB credentials (gitignored)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── app/
│   │   │       ├── Main.java
│   │   │       ├── gui/
│   │   │       │   ├── ShopFrame.java
│   │   │       │   ├── ProductFormPanel.java
│   │   │       │   ├── ProductTablePanel.java
│   │   │       │   ├── SearchPanel.java
│   │   │       │   └── components/               # Specialized component classes
│   │   │       ├── model/
│   │   │       │   ├── Product.java
│   │   │       │   └── ProductDate.java          # Avoid conflict with java.util.Date
│   │   │       ├── database/
│   │   │       │   ├── DatabaseManager.java      # Manages JDBC connections
│   │   │       │   ├── dao/
│   │   │       │   │   ├── ProductDao.java       # CRUD interface
│   │   │       │   │   └── ProductDaoImpl.java   # Implementation using JDBC
│   │   │       │   ├── migration/
│   │   │       │   │   └── MigrationRunner.java  # Runs SQL scripts automatically
│   │   │       │   └── mapper/
│   │   │       │       └── ProductRowMapper.java # Maps ResultSet to Product
│   │   │       └── util/
│   │   │           ├── ValidationUtils.java
│   │   │           ├── GuiUtils.java
│   │   │           └── DateUtils.java
│   │   └── resources/
│   │       ├── static/                           
│   │       └── dynamic/                          # Logs and temporary files
│   └── test/
│       ├── java/
│       │   ├── integration/
│       │   └── unit/
│       └── resources/
└── docs/                                         # Notes, user manual, screenshots
```

---

## ⚙️ Setup & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/MYS158/shop-project.git
   cd shop-project
   ```

2. **Configure the database** in `config/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/shopdb
   db.user=root
   db.password=yourpassword
   ```

3. **Initialize the database:**
   ```bash
   mysql -u root -p shopdb < sql/schema.sql
   mysql -u root -p shopdb < sql/seed.sql
   ```

4. **Build and run the project:**
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="app.Main"
   ```

---

## 📦 Dependencies

- **mysql-connector-j:** 9.5.0  
- **jcalendar:** 1.4  
- **JUnit:** 5.10  
- **Maven Compiler Plugin:** Java 17  

---

## 🧠 Design Notes

The project follows an **MVC (Model-View-Controller)** pattern:
- **Model:** Product entities and validation logic.
- **View:** Swing UI panels and components.
- **Controller:** DAO classes and service logic.

---

## 👤 Author
**Miguel Muñoz**  
**Version:** 1.0.0  
**Last Updated:** October 2025  
**License:** MIT License (see LICENSE file)

---

> *A clean, modular, and maintainable Java project for managing product catalogs with database persistence and GUI interaction.*