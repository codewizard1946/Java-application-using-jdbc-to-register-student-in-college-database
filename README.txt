# Employee Records to CSV Exporter (Java + JDBC)

## Project objective

This Java application connects to a MySQL database using JDBC, retrieves employee records with a `ResultSet`, and writes those records to a CSV file named `employees.csv`. It handles database errors and file-writing errors separately.

## Files in this repository

- `.gitignore` — excludes compiled files, local credentials, downloaded JARs, and generated CSV output.
- `Main.java` — Java application that reads employee rows and exports them to CSV.
- `employee_schema.sql` — creates the database and table, and adds sample employee records.
- `README.md` — project description and setup instructions.

## Technologies used

- Java (JDK 8 or newer)
- JDBC
- MySQL
- MySQL Connector/J JDBC driver
- CSV (comma-separated values)

## Database structure

Database: `employee_db`

Table: `employees`

| Column | Meaning |
|---|---|
| `employee_id` | Employee ID (primary key) |
| `full_name` | Employee name |
| `department` | Department name |
| `email` | Employee email address |
| `salary` | Employee salary |

The SQL script uses example employee data. Re-running it skips rows that conflict with an existing employee ID or email; it does not overwrite existing records. Replace the examples with authorized data for your own project when appropriate.

## How the program works

1. JDBC opens a connection to MySQL.
2. A `PreparedStatement` runs a `SELECT` query on the `employees` table.
3. `ResultSet` reads the query results row by row.
4. The application writes a CSV header and each employee row to `employees.csv`.
5. SQL/database problems and file-writing problems are reported with separate error messages.

## Setup and run (when you are ready)

### 1. Prepare the database

Open `employee_schema.sql` in MySQL Workbench and execute it. It creates `employee_db`, creates the `employees` table, and inserts five example records.

### 2. Add the JDBC driver locally

Download **MySQL Connector/J** from the official MySQL website. The `ojdbc` JAR is for Oracle Database and will not work as the MySQL driver for this project.

Create a folder named `lib` beside `Main.java`, and place the downloaded MySQL Connector/J JAR inside it. The `lib` folder and JAR files are intentionally excluded from GitHub by `.gitignore`.

### 3. Set database credentials in Windows PowerShell

Use your actual local MySQL username and password. These commands apply only to the current PowerShell terminal:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/employee_db"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

Change `root` or the password if your MySQL setup uses different credentials. Do not publish real passwords in this repository.

### 4. Compile and run in Windows PowerShell

Run these commands from the project folder, where `Main.java` and the `lib` folder are located:

```powershell
javac -cp ".;lib/*" Main.java
java -cp ".;lib/*" Main
```

After a successful run, `employees.csv` appears in the project folder. You can open it with Excel or a text editor. The CSV file is ignored by Git by default, so it will not be committed automatically.

For macOS/Linux, use a colon instead of a semicolon in the classpath:

```bash
javac -cp ".:lib/*" Main.java
java -cp ".:lib/*" Main
```

## Error handling

- **Database error:** check that MySQL is running, the SQL script has been executed, the JDBC driver is available, and the connection settings are correct.
- **File error:** check that the output folder exists and the program has permission to write to it.

## GitHub note

Uploading these four files to GitHub stores and displays the source code; GitHub does not automatically run the Java program or connect it to your local MySQL database. You can create the repository and upload the files without running the project first.
