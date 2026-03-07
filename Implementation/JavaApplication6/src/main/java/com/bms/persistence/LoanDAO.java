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

import com.bms.domain.entity.Loan;

/**
 * LoanDAO - Data Access Object for Loan entity
 * All SQL queries and JDBC operations are confined to this layer
 */
public class LoanDAO {
    private final DataSource dataSource;

    public LoanDAO() {
        this.dataSource = DataSourceFactory.getInstance().getDataSource();
    }

    /**
     * Find all loans for a customer
     */
    public List<Loan> findByCustomer(int customerId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id, customer_id, amount, loan_type, duration_months, " +
                "purpose, interest_rate, status, submission_date, decision_date " +
                "FROM [Loan] WHERE customer_id = ? ORDER BY submission_date DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding loans for customer: " + e.getMessage());
            e.printStackTrace();
        }

        return loans;
    }

    /**
     * Find loans by customer and status filter
     */
    public List<Loan> findByCustomerAndStatus(int customerId, String status) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id, customer_id, amount, loan_type, duration_months, " +
                "purpose, interest_rate, status, submission_date, decision_date " +
                "FROM [Loan] WHERE customer_id = ? AND status = ? ORDER BY submission_date DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding loans by status: " + e.getMessage());
            e.printStackTrace();
        }

        return loans;
    }

    /**
     * Find all loans with a given status (e.g. PENDING for admin review)
     */
    public List<Loan> findByStatus(String status) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id, customer_id, amount, loan_type, duration_months, " +
                "purpose, interest_rate, status, submission_date, decision_date " +
                "FROM [Loan] WHERE status = ? ORDER BY submission_date ASC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding loans by status: " + e.getMessage());
            e.printStackTrace();
        }

        return loans;
    }

    /**
     * Find a loan by ID
     */
    public Loan findById(int loanId) {
        String sql = "SELECT loan_id, customer_id, amount, loan_type, duration_months, " +
                "purpose, interest_rate, status, submission_date, decision_date " +
                "FROM [Loan] WHERE loan_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loanId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding loan: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Insert a new loan application
     * 
     * @return the generated loan_id, or -1 if failed
     */
    public int insert(Loan loan) {
        String sql = "INSERT INTO [Loan] (customer_id, amount, loan_type, duration_months, " +
                "purpose, interest_rate, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getCustomerId());
            stmt.setDouble(2, loan.getAmount());
            stmt.setString(3, loan.getLoanType());
            stmt.setInt(4, loan.getDurationMonths());
            stmt.setString(5, loan.getPurpose());
            stmt.setDouble(6, loan.getInterestRate());
            stmt.setString(7, loan.getStatus() != null ? loan.getStatus() : "PENDING");

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting loan: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Update loan decision (approve/reject)
     */
    public boolean updateDecision(int loanId, String newStatus, LocalDateTime decisionDate) {
        String sql = "UPDATE [Loan] SET status = ?, decision_date = ? WHERE loan_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(decisionDate));
            stmt.setInt(3, loanId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating loan decision: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Map a ResultSet row to a Loan object
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setCustomerId(rs.getInt("customer_id"));
        loan.setAmount(rs.getDouble("amount"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setDurationMonths(rs.getInt("duration_months"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setStatus(rs.getString("status"));

        Timestamp submissionDate = rs.getTimestamp("submission_date");
        if (submissionDate != null) {
            loan.setSubmissionDate(submissionDate.toLocalDateTime());
        }

        Timestamp decisionDate = rs.getTimestamp("decision_date");
        if (decisionDate != null) {
            loan.setDecisionDate(decisionDate.toLocalDateTime());
        }

        return loan;
    }
}