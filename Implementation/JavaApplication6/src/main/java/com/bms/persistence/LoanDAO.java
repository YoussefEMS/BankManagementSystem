package com.bms.persistence;

import java.time.LocalDateTime;
import java.util.Set;

import com.bms.domain.entity.Loan;

/**
 * 
 */
public class LoanDAO {

    /**
     * Default constructor
     */
    public LoanDAO() {
    }


    /**
     * @param customerId 
     * @return
     */
    public Set<Loan> findByCustomer(int customerId) {
        // TODO implement here
        return null;
    }

    /**
     * @param customerId 
     * @param status 
     * @return
     */
    public Set<Loan> findByCustomerAndStatus(int customerId, String status) {
        // TODO implement here
        return null;
    }

    /**
     * @param loan 
     * @return
     */
    public int insert(Loan loan) {
        // TODO implement here
        return 0;
    }

    /**
     * @param status 
     * @return
     */
    public Set<Loan> findByStatus(String status) {
        // TODO implement here
        return null;
    }

    /**
     * @param loanId 
     * @return
     */
    public Loan findById(int loanId) {
        // TODO implement here
        return null;
    }

    /**
     * @param loanId 
     * @param newStatus 
     * @param decisionDate 
     * @return
     */
    public void updateDecision(int loanId, String newStatus, LocalDateTime decisionDate) {
        // TODO implement here
    }

}