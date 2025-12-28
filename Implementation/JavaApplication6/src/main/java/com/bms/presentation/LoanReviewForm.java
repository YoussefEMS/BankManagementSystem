package com.bms.presentation;

import java.util.Date;
import java.util.Set;

import com.bms.domain.entity.Loan;

/**
 * 
 */
public class LoanReviewForm {

    /**
     * Default constructor
     */
    public LoanReviewForm() {
    }

    /**
     * 
     */
    private int selectedLoanId;

    /**
     * 
     */
    private Set<Loan> pendingLoans;

    /**
     * 
     */
    private Loan loanDetails;


    /**
     * @return
     */
    public void openPendingLoans() {
        // TODO implement here
    }

    /**
     * @param loanId 
     * @return
     */
    public void selectLoan(int loanId) {
        // TODO implement here
    }

    /**
     * @param loanId 
     * @param decision 
     * @return
     */
    public void submitDecision(int loanId, String decision) {
        // TODO implement here
    }

    /**
     * @param loans 
     * @return
     */
    public void displayLoanList(Set<Loan> loans) {
        // TODO implement here
    }

    /**
     * @param loan 
     * @return
     */
    public void displayLoanDetails(Loan loan) {
        // TODO implement here
    }

    /**
     * @param loanId 
     * @param newStatus 
     * @param decisionDate 
     * @return
     */
    public void displayDecisionSuccess(int loanId, String newStatus, Date decisionDate) {
        // TODO implement here
    }

}