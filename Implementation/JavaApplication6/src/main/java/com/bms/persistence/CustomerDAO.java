package com.bms.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.bms.domain.entity.Customer;

/**
 * CustomerDAO - Data Access Object for Customer entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class CustomerDAO {
    private final DataSource dataSource;

    /**
     * Constructor initializes DataSource
     */
    public CustomerDAO() {
        this.dataSource = DataSourceFactory.getInstance().getDataSource();
    }

    /**
     * Authenticate a customer with email and password
     * 
     * @param email    customer's email
     * @param password customer's password (plain text)
     * @return Customer object if authentication succeeds, null otherwise
     */
    public Customer authenticate(String email, String password) {
        String sql = "SELECT customer_id, full_name, national_id, email, password, " +
                "phone, address, tier, role, status, date_created " +
                "FROM [Customer] WHERE email = ? AND status = 'ACTIVE'";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // Simple password comparison (in production, use bcrypt or similar)
                    if (storedPassword.equals(password)) {
                        return mapResultSetToCustomer(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating customer: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Authentication failed
    }

    /**
     * Find a customer by ID
     * 
     * @param customerId the customer ID
     * @return Customer object if found, null otherwise
     */
    public Customer findById(int customerId) {
        String sql = "SELECT customer_id, full_name, national_id, email, password, " +
                "phone, address, tier, role, status, date_created " +
                "FROM [Customer] WHERE customer_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if a customer exists by email
     * 
     * @param emailAddress the email address
     * @return true if customer exists, false otherwise
     */
    public boolean existsByEmail(String emailAddress) {
        String sql = "SELECT 1 FROM [Customer] WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailAddress);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if email exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Insert a new customer
     * 
     * @param customer the customer to insert
     * @return the generated customer ID, or -1 if failed
     */
    public int insert(Customer customer) {
        String sql = "INSERT INTO [Customer] (full_name, national_id, email, password, phone, address, tier, role, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getNationalID());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPassword());
            stmt.setString(5, customer.getPhoneNumber());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getTier());
            stmt.setString(8, customer.getRole() != null ? customer.getRole() : "CUSTOMER");

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting customer: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Map a ResultSet row to a Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setNationalID(rs.getString("national_id"));
        customer.setEmail(rs.getString("email"));
        customer.setPassword(rs.getString("password"));
        customer.setPhoneNumber(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setTier(rs.getString("tier"));
        customer.setRole(rs.getString("role"));
        customer.setStatus(rs.getString("status"));
        return customer;
    }
}