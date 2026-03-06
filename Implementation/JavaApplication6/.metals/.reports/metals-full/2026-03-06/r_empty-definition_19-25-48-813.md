error id: file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/TransactionDAO.java:_empty_/DataSourceFactory#
file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/TransactionDAO.java
empty definition using pc, found symbol in pc: _empty_/DataSourceFactory#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 621
uri: file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/TransactionDAO.java
text:
```scala
package com.bms.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.bms.domain.entity.Transaction;

/**
 * TransactionDAO - Data Access Object for Transaction entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class TransactionDAO {
    private final DataSource dataSource;

    public TransactionDAO() {
        this.dataSource = @@DataSourceFactory.getInstance().getDataSource();
    }

    /**
     * Find transactions by account number with optional filters
     * @param accountNumber the account number
     * @param startDateTime optional start date/time (inclusive)
     * @param endDateTime optional end date/time (inclusive)
     * @param typeFilter optional transaction type filter (or "All" for no filter)
     * @return List of transactions, empty if none found
     */
    public List<Transaction> findByAccountNo(String accountNumber, 
                                             LocalDateTime startDateTime,
                                             LocalDateTime endDateTime,
                                             String typeFilter) {
        List<Transaction> transactions = new ArrayList<>();
        
        // Build dynamic SQL based on provided filters
        StringBuilder sql = new StringBuilder(
            "SELECT transaction_id, account_number, type, amount, timestamp, " +
            "performed_by, note, balance_after, reference_code " +
            "FROM [Transactions] WHERE account_number = ?"
        );
        
        // Add date filters if provided
        if (startDateTime != null) {
            sql.append(" AND timestamp >= ?");
        }
        if (endDateTime != null) {
            sql.append(" AND timestamp <= ?");
        }
        
        // Add type filter if provided and not "All"
        if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("All")) {
            sql.append(" AND type = ?");
        }
        
        // Order by timestamp descending (most recent first)
        sql.append(" ORDER BY timestamp DESC");
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setString(paramIndex++, accountNumber);
            
            if (startDateTime != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(startDateTime));
            }
            if (endDateTime != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(endDateTime));
            }
            if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("All")) {
                stmt.setString(paramIndex++, typeFilter);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }

    /**
     * Map a ResultSet row to a Transaction object
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setAccountNumber(rs.getString("account_number"));
        transaction.setType(rs.getString("type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            transaction.setTimestamp(timestamp.toLocalDateTime());
        }
        
        transaction.setPerformedBy(rs.getString("performed_by"));
        transaction.setNote(rs.getString("note"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setReferenceCode(rs.getString("reference_code"));
        
        return transaction;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/DataSourceFactory#