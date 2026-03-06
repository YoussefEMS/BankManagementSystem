error id: file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/AccountDAO.java:_empty_/DataSourceFactory#
file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/AccountDAO.java
empty definition using pc, found symbol in pc: _empty_/DataSourceFactory#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 598
uri: file:///C:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/persistence/AccountDAO.java
text:
```scala
package com.bms.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.bms.domain.entity.Account;

/**
 * AccountDAO - Data Access Object for Account entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class AccountDAO {
    private final DataSource dataSource;

    public AccountDAO() {
        this.dataSource = @@DataSourceFactory.getDataSource();
    }

    /**
     * Find all accounts for a customer
     * @param customerId the customer ID
     * @return List of Account objects for the customer
     */
    public List<Account> findByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_number, customer_id, account_type, balance, currency, status, date_opened " +
                     "FROM [Account] WHERE customer_id = ? ORDER BY date_opened DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding accounts for customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return accounts;
    }

    /**
     * Find an account by account number
     * @param accountNumber the account number to search for
     * @return Account if found, null otherwise
     */
    public Account findByAccountNo(String accountNumber) {
        String sql = "SELECT account_number, customer_id, account_type, balance, currency, status, date_opened " +
                     "FROM [Account] WHERE account_number = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Not found
    }

    /**
     * Map a ResultSet row to an Account object
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(rs.getString("account_number"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setCurrency(rs.getString("currency"));
        account.setStatus(rs.getString("status"));
        
        Timestamp dateOpened = rs.getTimestamp("date_opened");
        if (dateOpened != null) {
            account.setDateOpened(dateOpened.toLocalDateTime());
        }
        
        return account;
    }

    /**
     * Update account balance (optional, not required for UC-02 and UC-04)
     */
    public void updateBalance(String accountNumber, BigDecimal newBalance) {
        String sql = "UPDATE [Account] SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/DataSourceFactory#