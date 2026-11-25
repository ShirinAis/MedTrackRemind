package medtrack.db;

import medtrack.graph.InteractionGraph;
import medtrack.model.Drug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Үйлеспейтін дәрі жұптары (1-студент). */
public class InteractionRepository {

    /** Базадағы бір жол. */
    public record Pair(long id, String a, String b) {}

    private final Database db;

    public InteractionRepository(Database db) { this.db = db; }

    public void addConflict(String a, String b) {
        String x = a.trim().toLowerCase();
        String y = b.trim().toLowerCase();
        if (x.compareTo(y) > 0) { String t = x; x = y; y = t; }
        String sql = "INSERT OR IGNORE INTO interactions(drug_a, drug_b) VALUES(?, ?)";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, x);
            ps.setString(2, y);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** id-сы afterId-ден үлкен жолдар (жаңадан қосылғандар). */
    public List<Pair> findAfter(long afterId) {
        String sql = "SELECT id, drug_a, drug_b FROM interactions WHERE id > ? ORDER BY id";
        List<Pair> list = new ArrayList<>();
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, afterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Pair(rs.getLong(1), rs.getString(2), rs.getString(3)));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return list;
    }

    public List<Pair> findAll() { return findAfter(0); }

    /** Бүкіл базаны графқа жүктейді. */
    public InteractionGraph loadGraph() {
        InteractionGraph g = new InteractionGraph();
        for (Pair p : findAll()) {
            g.addConflict(Drug.ofName(p.a()), Drug.ofName(p.b()));
        }
        return g;
    }
}
