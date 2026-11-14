/*
 * Centralized Database Connection Helper
 * Project: National Scholarship Portal (NSP)
 *
 * Provides a single point of configuration and lifecycle management
 * for JDBC connections to MySQL database `nsp_db`.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database connection parameters
    // Standard XAMPP default: user 'root' with empty password ""
    // If you configured a MySQL password during MySQL Server install (e.g., 'root' or 'admin123'),
    // update PASSWORD below or pass -Ddb.password=yourpassword
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DB_NAME = "nsp_db";
    private static final String DB_HOST = System.getProperty("db.host", "localhost");
    private static final String DB_PORT = System.getProperty("db.port", "3306");
    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private static final String USER = System.getProperty("db.user", "root");
    private static final String PASSWORD = System.getProperty("db.password", "");

    // Static initializer to load driver once
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] WARNING: MySQL JDBC Driver (" + DRIVER_CLASS + ") not found in classpath!");
            System.err.println("[DBConnection] Please ensure 'mysql-connector-j-x.x.x.jar' is added to project libraries.");
        }
    }

    /**
     * Establishes and returns a new active Connection to the MySQL database.
     * @return Connection object
     * @throws SQLException with descriptive troubleshooting guidance if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            String diagnosticMessage = buildDiagnosticMessage(ex);
            System.err.println(diagnosticMessage);
            throw new SQLException(diagnosticMessage, ex);
        }
    }

    /**
     * Diagnostic helper providing clear actionable steps for common MySQL connection issues.
     */
    private static String buildDiagnosticMessage(SQLException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to connect to MySQL database '").append(DB_NAME).append("'.\n");
        sb.append("Technical error: ").append(ex.getMessage()).append("\n\n");
        sb.append("Troubleshooting Checklist:\n");
        sb.append("1. Is MySQL Server running? (e.g., Start MySQL in XAMPP Control Panel or Windows Services).\n");
        sb.append("2. Has 'schema.sql' been executed to create 'nsp_db'?\n");
        sb.append("3. Check credentials in DBConnection.java (Current -> Host: ").append(DB_HOST)
          .append(":").append(DB_PORT).append(", User: '").append(USER).append("').\n");
        sb.append("4. Verify that mysql-connector-j JAR is added to classpath.\n");
        return sb.toString();
    }

    /**
     * Tests whether the database connection is currently available.
     * @return true if connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Safe resource cleanup helper for Statement, ResultSet, and Connection.
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Standalone CLI test utility. Run directly to verify database health.
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Testing MySQL Connection to: " + URL);
        System.out.println(" Username: " + USER);
        System.out.println("==================================================");

        try (Connection conn = getConnection()) {
            System.out.println(" SUCCESS: Connected to MySQL database successfully!");
            System.out.println(" Catalog / Database: " + conn.getCatalog());
            System.out.println(" Driver Name       : " + conn.getMetaData().getDriverName());
            System.out.println(" Driver Version    : " + conn.getMetaData().getDriverVersion());
        } catch (SQLException e) {
            System.err.println("\n CONNECTION FAILED:\n" + e.getMessage());
        }
    }
}
