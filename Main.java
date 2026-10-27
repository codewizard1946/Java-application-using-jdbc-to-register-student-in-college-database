import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Retrieves employee records from a MySQL database and exports them to a CSV file.
 * Database settings can be supplied through DB_URL, DB_USER, and DB_PASSWORD
 * environment variables so passwords do not need to be stored in this source file.
 */
public class Main {
    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/employee_db";
    private static final String DEFAULT_DB_USER = "root";

    private static final String EMPLOYEE_QUERY =
            "SELECT employee_id, full_name, department, email, salary " +
            "FROM employees ORDER BY employee_id";

    public static void main(String[] args) {
        String databaseUrl = getSetting("DB_URL", DEFAULT_DB_URL);
        String databaseUser = getSetting("DB_USER", DEFAULT_DB_USER);
        String databasePassword = getSetting("DB_PASSWORD", "");

        // You can optionally pass a different CSV filename as a command-line argument.
        Path csvPath = Paths.get(args.length > 0 ? args[0] : "employees.csv");

        exportEmployees(databaseUrl, databaseUser, databasePassword, csvPath);
    }

    private static void exportEmployees(
            String databaseUrl,
            String databaseUser,
            String databasePassword,
            Path csvPath) {

        int exportedCount = 0;

        try (
                Connection connection = DriverManager.getConnection(
                        databaseUrl, databaseUser, databasePassword);
                PreparedStatement statement = connection.prepareStatement(EMPLOYEE_QUERY);
                ResultSet resultSet = statement.executeQuery();
                BufferedWriter writer = Files.newBufferedWriter(
                        csvPath, StandardCharsets.UTF_8)) {

            // Write the CSV column headings first.
            writer.write("Employee ID,Full Name,Department,Email,Salary");
            writer.newLine();

            // ResultSet lets us read one database row at a time.
            while (resultSet.next()) {
                String[] employee = {
                        resultSet.getString("employee_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("department"),
                        resultSet.getString("email"),
                        resultSet.getString("salary")
                };

                writer.write(toCsvRow(employee));
                writer.newLine();
                exportedCount++;
            }

            System.out.println("Export completed successfully.");
            System.out.println("Employee records exported: " + exportedCount);
            System.out.println("CSV file location: " + csvPath.toAbsolutePath());

        } catch (SQLException e) {
            System.err.println("Database error: Could not retrieve employee records.");
            System.err.println("Check that MySQL is running, the database/table exist, " +
                    "and DB_URL, DB_USER, and DB_PASSWORD are correct.");
            System.err.println("Details: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File error: Could not write the CSV file.");
            System.err.println("Check that the folder exists and you have permission to write there.");
            System.err.println("Details: " + e.getMessage());
        }
    }

    /** Returns the environment variable value, or a default if it is missing/blank. */
    private static String getSetting(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    /** Converts a row into CSV format and safely escapes commas, quotes, and line breaks. */
    private static String toCsvRow(String[] values) {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                row.append(',');
            }

            String value = values[i] == null ? "" : values[i];
            boolean needsQuotes = value.contains(",")
                    || value.contains("\"")
                    || value.contains("\n")
                    || value.contains("\r");

            if (needsQuotes) {
                row.append('"');
                row.append(value.replace("\"", "\"\""));
                row.append('"');
            } else {
                row.append(value);
            }
        }

        return row.toString();
    }
}
