package medtrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** SQLite қосылымы және кестелерді жасау (1-студент). Құпиясөз жоқ, тек файл жолы. */
public class Database {
    private final String url;

    public Database(String filePath) {
        this.url = "jdbc:sqlite:" + filePath;
        init();
    }

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        // бірнеше ағын/клиент бір базамен жұмыс істегенде "database is locked" болмауы үшін
        try (PreparedStatement ps = c.prepareStatement("PRAGMA busy_timeout = 5000")) {
            ps.execute();
        }
        return c;
    }

    private void init() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS drugs ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE COLLATE NOCASE, "
                + "form TEXT NOT NULL, dose_mg REAL NOT NULL)",
            "CREATE TABLE IF NOT EXISTS interactions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "drug_a TEXT NOT NULL, drug_b TEXT NOT NULL, "
                + "UNIQUE(drug_a, drug_b))",
            "CREATE TABLE IF NOT EXISTS schedule ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "drug_name TEXT NOT NULL, dosage_type TEXT NOT NULL, "
                + "amount REAL NOT NULL, time TEXT NOT NULL)"
        };
        try (Connection c = connect()) {
            for (String sql : ddl) {
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Базаны жасау қатесі", e);
        }
    }
}
