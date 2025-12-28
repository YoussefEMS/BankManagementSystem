package com.bms.domain.controller;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;

/**
 * 
 */
public class DepositHandler {

    /**
     * Default constructor
     */
    public DepositHandler() {
    }






    /**
     * @param accountNo 
     * @param amount 
     * @param description 
     * @return
     */
    public int postDeposit(String accountNo, double amount, String description) {
        // TODO implement here
        return 0;
    }

    /**
     * @param amount 
     * @return
     */
    private boolean validateAmount(double amount) {
        // TODO implement here
        return false;
    }

    /**
     * @param accountNo 
     * @return
     */
    private Account getAccount(String accountNo) {
        // TODO implement here
        return null;
    }

    /**
     * @param accountNo 
     * @param amount 
     * @param description 
     * @param balanceAfter 
     * @return
     */
    private Transaction recordDepositTx(String accountNo, double amount, String description, double balanceAfter) {
        // TODO implement here
        return null;
    }

}