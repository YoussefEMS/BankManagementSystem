package com.bms.domain.controller;

import com.bms.domain.entity.Transaction;

/**
 * 
 */
public class WithdrawCashController {

    /**
     * Default constructor
     */
    public WithdrawCashController() {
    }





    /**
     * @param accountNo 
     * @param amount 
     * @param description 
     * @return
     */
    public int withdrawCash(String accountNo, double amount, String description) {
        // TODO implement here
        return 0;
    }

    /**
     * @param accountNo 
     * @return
     */
    public boolean validateAccountExistsAndActive(String accountNo) {
        // TODO implement here
        return false;
    }

    /**
     * @param amount 
     * @return
     */
    public boolean validateAmount(double amount) {
        // TODO implement here
        return false;
    }

    /**
     * @param actorId 
     * @param accountNo
     */
    public void authorizeWithdraw(String actorId, String accountNo) {
        // TODO implement here
    }

    /**
     * @param accountNo 
     * @param amount 
     * @return
     */
    public boolean checkSufficientFunds(String accountNo, double amount) {
        // TODO implement here
        return false;
    }

    /**
     * @param accountNo 
     * @param amount 
     * @param description 
     * @param balanceAfter 
     * @return
     */
    public Transaction recordWithdrawalTx(String accountNo, double amount, String description, double balanceAfter) {
        // TODO implement here
        return null;
    }

}