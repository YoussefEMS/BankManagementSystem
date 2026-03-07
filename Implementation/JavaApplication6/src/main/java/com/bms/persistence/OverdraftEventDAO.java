package com.bms.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.bms.domain.entity.OverdraftEvent;

/**
 * OverdraftEventDAO - Data Access Object for OverdraftEvent entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class OverdraftEventDAO {
    private final DataSource dataSource;

    public OverdraftEventDAO() {
        this.dataSource = DataSourceFactory.getInstance().getDataSource();
    }

    /**
     * Insert a new overdraft event
     * 
     * @return the generated overdraft_id, or -1 if failed
     */
    public int insert(OverdraftEvent evt) {
        String sql = "INSERT INTO [OverdraftEvent] (account_number, transaction_id, amount, " +
                "timestamp, alert_sent) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, evt.getAccountNumber());
            stmt.setInt(2, evt.getTransactionId());
            stmt.setDouble(3, evt.getAmount());
            stmt.setTimestamp(4, evt.getTimestamp() != null
                    ? Timestamp.valueOf(evt.getTimestamp())
                    : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(5, evt.isAlertSent());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting overdraft event: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Find all overdraft events (for admin view)
     */
    public List<OverdraftEvent> findAll() {
        List<OverdraftEvent> events = new ArrayList<>();
        String sql = "SELECT overdraft_id, account_number, transaction_id, amount, " +
                "timestamp, alert_sent FROM [OverdraftEvent] ORDER BY timestamp DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OverdraftEvent evt = new OverdraftEvent();
                evt.setOverdraftId(rs.getInt("overdraft_id"));
                evt.setAccountNumber(rs.getString("account_number"));
                evt.setTransactionId(rs.getInt("transaction_id"));
                evt.setAmount(rs.getDouble("amount"));

                Timestamp ts = rs.getTimestamp("timestamp");
                if (ts != null) {
                    evt.setTimestamp(ts.toLocalDateTime());
                }

                evt.setAlertSent(rs.getBoolean("alert_sent"));
                events.add(evt);
            }
        } catch (SQLException e) {
            System.err.println("Error finding overdraft events: " + e.getMessage());
            e.printStackTrace();
        }

        return events;
    }
}