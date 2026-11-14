# National Scholarship Portal (NSP) - Java Swing & MySQL System

**Original Author:** Kapil Tamoli  
**System Architecture & Upgrades:** Senior Java Developer  
**Technologies:** Java (Swing GUI), MySQL Database, JDBC (`PreparedStatement`), Modern UI Theme  

---

## 🚀 Project Overview

The **National Scholarship Portal (NSP)** is a complete desktop application built using **Java Swing** and **MySQL**. It features:
- **Role-Based Authentication**: Secure login for both **Students** and **Administrators** with input validation and session tracking.
- **Student Registration**: Input validation, duplicate username/email detection, and insertion into the `users` table.
- **Scholarship Application**: Multi-field submission (Student Name, Year, Course, Reservation Category, Numeric Annual Family Income) saved with status `'Pending'`.
- **Live Status Tracking**: Real-time tabular tracking of scholarship applications with color-coded status badges (*Pending*, *Approved*, *Rejected*).
- **Admin Control Center**: KPI dashboard (Total, Pending, Approved, Rejected) and a dedicated Management Table with real-time **Approve**, **Reject**, and **Reset to Pending** actions that immediately update the MySQL database.
- **Modern UI Design System (`UITheme.java`)**: Professional typography, custom button palettes with hover effects, native Look-and-Feel, padded input fields, and zebra-striped JTables.

---

## 📁 Project Directory Structure

```text
nsp_project/
├── schema.sql              # Complete MySQL script (Creates `nsp_db`, tables, admin, demo data)
├── run.bat                 # One-click Windows compilation & launch script
├── test_db.bat             # Quick utility to verify database connectivity
├── lib/
│   └── README.txt          # Instructions and download links for mysql-connector-j.jar
├── src/
│   ├── Main.java           # Entry point (initializes Look & Feel and launches Login)
│   ├── DBConnection.java   # Centralized JDBC connection manager (com.mysql.cj.jdbc.Driver)
│   ├── UITheme.java        # Modern UI styling, color palette, buttons & JTable cell renderers
│   ├── Login.java          # Role-based login (Student Login & Admin Login)
│   ├── Register.java       # Student registration with validation & duplicate checks
│   ├── StudentDashboard.java # Student home page with real-time application count & status
│   ├── ApplyScholarship.java # Scholarship submission form with numeric income validation
│   ├── ViewStatus.java     # Real-time tabular view of student applications
│   ├── AdminDashboard.java # Admin KPI dashboard showing status counts
│   └── ViewApplications.java # Admin management table with Approve/Reject/Filter controls
└── README.md               # Complete documentation and viva preparation guide
```

---

## 🗄️ Database Setup Instructions

### 1. Start MySQL Server
- If using **XAMPP**: Open XAMPP Control Panel and click **Start** next to **MySQL** (and **Apache** if using phpMyAdmin).
- If using **MySQL Workbench / Server**: Ensure the MySQL Windows Service is running on port `3306`.

### 2. Import `schema.sql`
You can import the database script using any of the following methods:

#### Method A: Using phpMyAdmin (Browser)
1. Open your browser and navigate to `http://localhost/phpmyadmin/`.
2. Click the **Import** tab at the top.
3. Click **Choose File** and select `schema.sql` from the project root.
4. Click **Go** (or **Import**) at the bottom. The database `nsp_db` and tables `users` and `applications` will be created automatically.

#### Method B: Using MySQL Command Line Client
```bash
mysql -u root -p < schema.sql
```
*(Press Enter if you do not have a root password)*

#### Method C: Using MySQL Workbench
1. Open MySQL Workbench and connect to your local MySQL instance.
2. Go to **File -> Open SQL Script...** and select `schema.sql`.
3. Click the ⚡ **Execute** (lightning bolt) icon to run the script.

---

## 🔑 Default Credentials for Testing & Viva

| Role | Username / Email | Password | Full Name | Access Scope |
| :--- | :--- | :--- | :--- | :--- |
| **Admin** | `admin` *(or admin@nsp.gov.in)* | `admin123` | System Administrator | Full admin controls, Approve/Reject applications |
| **Student** | `kapil` *(or kapil@example.com)* | `kapil123` | Kapil Tamoli | Apply, view submitted applications |
| **Student** | `riya` *(or riya@example.com)* | `riya123` | Riya Sharma | Demo student account |
| **Student** | `aman` *(or aman@example.com)* | `aman123` | Aman Verma | Demo student account |

---

## ⚙️ How to Configure Database Credentials (`DBConnection.java`)

Open `src/DBConnection.java` to verify or change database settings if your local MySQL uses a custom password:
```java
private static final String URL = "jdbc:mysql://localhost:3306/nsp_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = ""; // Change to "root" or your MySQL password if set
```

---

## 💻 How to Open and Run in IntelliJ IDEA

1. **Open Project**:
   - Launch IntelliJ IDEA.
   - Click **Open** and select the folder `nsp_project` (or `nsp project`).
2. **Set Sources Root**:
   - Right-click the `src` folder in the Project view.
   - Select **Mark Directory as -> Sources Root**.
3. **Add MySQL Connector/J JAR**:
   - Download `mysql-connector-j-8.4.0.jar` (see `lib/README.txt`).
   - Place it into the `lib/` folder.
   - In IntelliJ, click **File -> Project Structure -> Libraries**.
   - Click the **+** (plus icon) -> **Java**.
   - Select the `mysql-connector-j-8.4.0.jar` file and click **OK** -> **Apply** -> **OK**.
4. **Run Application**:
   - Navigate to `src/Main.java`.
   - Click the green **Run** button (or press `Shift + F10`).
   - The modern Login page will launch!

---

## 🎓 Viva Questions & Answers (Senior Java Developer Level)

### Q1: What is JDBC and what role does `DBConnection.java` play?
**Answer:** JDBC (Java Database Connectivity) is a standard Java API that connects Java applications to relational databases. `DBConnection.java` implements a **centralized singleton helper** that defines the connection URL, driver class (`com.mysql.cj.jdbc.Driver`), and credentials in one location. This follows the **DRY (Don't Repeat Yourself)** principle so credentials and connection pooling/closing are maintained cleanly.

### Q2: Why did you use `PreparedStatement` instead of `Statement`?
**Answer:** 
1. **SQL Injection Protection**: `PreparedStatement` parameterizes queries using placeholders (`?`), ensuring input data is never executed as raw SQL commands.
2. **Performance Optimization**: The database compiles and optimizes the SQL execution plan once; future executions reuse the compiled plan with different parameters.
3. **Automatic Type Conversion & Escaping**: Special characters, dates, and numbers are correctly handled without manual quote concatenation.

### Q3: Explain the database relationship between `users` and `applications`.
**Answer:** The `users` table holds user credentials with a primary key (`id`) and unique `username`. The `applications` table references `users.username` through a foreign key constraint (`fk_app_user`) with `ON UPDATE CASCADE ON DELETE CASCADE`. When a student logs in, queries filter `WHERE username = ?`, guaranteeing that students can only view their own applications, while administrators can query all applications.

### Q4: How does real-time status update work when an Admin clicks "Approve"?
**Answer:** 
1. The admin selects an application row in `ViewApplications.java`.
2. The hidden database primary key `id` is retrieved from the `DefaultTableModel`.
3. A confirmation dialog verifies the admin's intention.
4. A `PreparedStatement` executes: `UPDATE applications SET status = 'Approved' WHERE id = ?`.
5. Upon confirmation of rows affected (`> 0`), the table calls `loadApplicationsFromDB()`, re-fetching the updated status from MySQL and instantly refreshing the UI.

### Q5: What is the purpose of `SwingUtilities.invokeLater()` in `Main.java`?
**Answer:** Swing is single-threaded and not thread-safe. All GUI creation and component modifications must occur on the **Event Dispatch Thread (EDT)**. `SwingUtilities.invokeLater()` places the task on the EDT event queue, preventing concurrency glitches, race conditions, or screen tearing.

### Q6: How does the modern status badge renderer work?
**Answer:** In `UITheme.java`, a custom `DefaultTableCellRenderer` checks if `table.getColumnName(column)` equals `"Status"`. If true, it dynamically inspects the cell value and assigns a distinct colored badge (Amber for *Pending*, Green for *Approved*, Crimson Red for *Rejected*) along with zebra striping on alternating rows.
