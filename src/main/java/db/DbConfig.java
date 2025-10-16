package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConfig {
    public static Connection get() throws Exception {
        String url = getenv("DB_URL", "jdbc:mysql://localhost:3306/scholardb");
        String user = getenv("DB_USER", "root");
        String pass = getenv("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, pass);
    }

    private static String getenv(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? def : v;
    }
}
