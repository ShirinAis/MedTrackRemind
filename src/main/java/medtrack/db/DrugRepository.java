package medtrack.db;

import medtrack.model.Drug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Дәрілер кестесімен жұмыс (1-студент). Барлық сұрау PreparedStatement арқылы. */
public class DrugRepository {
    private final Database db;

    public DrugRepository(Database db) { this.db = db; }

    public void save(Drug d) {
        String sql = "INSERT OR IGNORE INTO drugs(name, form, dose_mg) VALUES(?, ?, ?)";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getForm());
            ps.setDouble(3, d.getDoseMg());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Drug> findAll() {
        String sql = "SELECT name, form, dose_mg FROM drugs ORDER BY name";
        List<Drug> list = new ArrayList<>();
        try (Connection c = db.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Drug.Builder().name(rs.getString(1))
                        .form(rs.getString(2)).doseMg(rs.getDouble(3)).build());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return list;
    }
}
