package com.bms.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import com.bms.domain.entity.InterestPosting;

/**
 * InterestPostingDAO - Data Access Object for InterestPosting entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class InterestPostingDAO {
    private final DataSource dataSource;

    public InterestPostingDAO() {
        this.dataSource = DataSourceProvider.getInstance().getDataSource();
    }

    /**
     * Insert a new interest posting record
     * 
     * @return the generated posting_id, or -1 if failed
     */
    public int insert(InterestPosting ip) {
        String sql = "INSERT INTO [InterestPosting] (account_number, amount, rate_used, timestamp) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ip.getAccountNumber());
            stmt.setDouble(2, ip.getAmount());
            stmt.setDouble(3, ip.getRateUsed());
            stmt.setTimestamp(4, ip.getTimestamp() != null
                    ? Timestamp.valueOf(ip.getTimestamp())
                    : Timestamp.valueOf(LocalDateTime.now()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting interest posting: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
}