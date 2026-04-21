package com.bms.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import com.bms.domain.entity.Transfer;

/**
 * TransferDAO - Data Access Object for Transfer entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class TransferDAO {
    private final DataSource dataSource;

    public TransferDAO() {
        this.dataSource = DataSourceProvider.getInstance().getDataSource();
    }

    /**
     * Insert a new transfer record
     * 
     * @return the generated transfer_id, or -1 if failed
     */
    public int insert(Transfer t) {
        String sql = "INSERT INTO [Transfer] (from_account_no, to_account_no, amount, " +
                "timestamp, reference_code, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getFromAccountNo());
            stmt.setString(2, t.getToAccountNo());
            stmt.setDouble(3, t.getAmount());
            stmt.setTimestamp(4, t.getTimestamp() != null
                    ? Timestamp.valueOf(t.getTimestamp())
                    : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(5, t.getReferenceCode());
            stmt.setString(6, t.getStatus() != null ? t.getStatus() : "COMPLETED");

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting transfer: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
}