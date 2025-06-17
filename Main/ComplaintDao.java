package sprint1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDao {

    /* ---------- helper: return ALL ids from a table ---------- */
    public List<Integer> listIds(String table, String column) throws SQLException {
        String sql = "SELECT " + column + " FROM " + table;
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<Integer> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getInt(1));
            return ids;
        }
    }

    /* ---------- insert one complaint, return generated id ---------- */
    public int insert(int userId,
                      int deptId,
                      int categoryId,
                      String title,
                      String description) throws SQLException {

        final String sql = """
                INSERT INTO complaints
                       (user_id, dept_id, category_id, title, description, status)
                VALUES (?,?,?,?,?,?)
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps =
                     c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            c.setAutoCommit(false);
            try {
                ps.setInt(1, userId);
                ps.setInt(2, deptId);
                ps.setInt(3, categoryId);
                ps.setString(4, title);
                ps.setString(5, description);
                ps.setString(6, "OPEN");
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                int id = keys.next() ? keys.getInt(1) : -1;
                c.commit();
                return id;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    /* ---------- keep your existing fetchId helper ---------- */
    public int fetchId(String sql, String param) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
}
