package com.bms.domain.controller;

import java.util.List;

import com.bms.domain.entity.Loan;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.LoanDAO;
import com.bms.persistence.ConfiguredDAOFactory;

/**
 * LoanStatusController - UC-11: View Loan Status
 * Allows customers to view their loan applications and statuses
 */
public class LoanStatusController {
    private final LoanDAO loanDAO;

    public LoanStatusController() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public LoanStatusController(DAOFactory factory) {
        this.loanDAO = factory.createLoanDAO();
    }

    /**
     * Get loans for a customer, optionally filtered by status
     * 
     * @param customerId the customer ID
     * @param filter     the status filter (null or "All" for no filter)
     * @return list of loans
     */
    public List<Loan> getLoansForCustomer(int customerId, String filter) {
        if (filter == null || filter.isEmpty() || filter.equalsIgnoreCase("All")) {
            return loanDAO.findByCustomer(customerId);
        }
        return loanDAO.findByCustomerAndStatus(customerId, filter);
    }
}
