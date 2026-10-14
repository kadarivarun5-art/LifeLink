package com.lifelink.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database access (JDBC) helper.
 * Uses the H2 embedded database, so no separate database server is needed.
 * The data file location can be changed with the DB_PATH environment variable.
 */
public final class DBUtil {

    private static final String DB_PATH = System.getenv("DB_PATH") != null
            ? System.getenv("DB_PATH")
            : System.getProperty("user.home") + "/lifelink-data/db";

    private static final String URL = "jdbc:h2:file:" + DB_PATH;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    /** Creates the tables on first start. Safe to call every time. */
    public static void initSchema() throws SQLException {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "full_name VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(150) NOT NULL UNIQUE,"
                    + "password_hash VARCHAR(128) NOT NULL,"
                    + "salt VARCHAR(64) NOT NULL,"
                    + "phone VARCHAR(20) NOT NULL,"
                    + "blood_group VARCHAR(5) NOT NULL,"
                    + "city VARCHAR(60) NOT NULL,"
                    + "available BOOLEAN DEFAULT TRUE NOT NULL,"
                    + "phone_visible BOOLEAN DEFAULT FALSE NOT NULL,"
                    + "email_verified BOOLEAN DEFAULT TRUE NOT NULL,"
                    + "verification_token_hash VARCHAR(64),"
                    + "verification_expires TIMESTAMP,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            // Safe migrations: existing accounts are NOT treated as email-verified automatically.
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT TRUE NOT NULL");
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token_hash VARCHAR(64)");
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_expires TIMESTAMP");
            // Safe migration for existing databases; existing donors stay private by default.
            st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS phone_visible BOOLEAN DEFAULT FALSE NOT NULL");
            st.execute("CREATE TABLE IF NOT EXISTS blood_requests ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id BIGINT NOT NULL,"
                    + "patient_name VARCHAR(100) NOT NULL,"
                    + "blood_group VARCHAR(5) NOT NULL,"
                    + "units INT NOT NULL,"
                    + "hospital VARCHAR(150) NOT NULL,"
                    + "city VARCHAR(60) NOT NULL,"
                    + "contact_phone VARCHAR(20) NOT NULL,"
                    + "needed_by DATE NOT NULL,"
                    + "note VARCHAR(255),"
                    + "status VARCHAR(10) DEFAULT 'OPEN' NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
        }
    }
}
