package com.bms.domain.controller;

import java.util.List;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Customer;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.CustomerDAO;

/**
 * AuthenticationController - Domain logic for authentication and session
 * management
 * Manages customer login, account access, and session state
 * Acts as intermediary between presentation and data layers
 */
public class AuthenticationController {
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private final AuthContext authContext;

    public AuthenticationController() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.authContext = AuthContext.getInstance();
    }

    /**
     * Authenticate a customer with email and password
     * 
     * @param email    customer's email
     * @param password customer's password
     * @return Customer object if authentication succeeds, null otherwise
     */
    public Customer authenticateCustomer(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        Customer customer = customerDAO.authenticate(email.trim(), password);
        if (customer != null && authContext.login(customer)) {
            return customer;
        }
        return null;
    }

    /**
     * Authenticate a customer for presentation layer
     * 
     * @param email    customer's email
     * @param password customer's password
     * @return true if authentication succeeds, false otherwise
     */
    public boolean authenticate(String email, String password) {
        return authenticateCustomer(email, password) != null;
    }

    /**
     * Get the currently logged-in customer ID
     * 
     * @return customer ID if logged in, -1 otherwise
     */
    public int getLoggedInCustomerId() {
        return authContext.getLoggedInCustomerId();
    }

    /**
     * Get the currently logged-in customer
     * 
     * @return Customer object if logged in, null otherwise
     */
    public Customer getLoggedInCustomer() {
        return authContext.getLoggedInCustomer();
    }

    /**
     * Get all accounts for the logged-in customer
     * 
     * @return List of accounts for the customer, empty list if not logged in
     */
    public List<Account> getLoggedInCustomerAccounts() {
        int customerId = authContext.getLoggedInCustomerId();
        if (customerId <= 0) {
            return List.of();
        }
        return accountDAO.findByCustomerId(customerId);
    }

    /**
     * Get account numbers as strings for the logged-in customer
     * Useful for populating dropdown menus
     * 
     * @return Array of account numbers, empty array if not logged in
     */
    public String[] getLoggedInCustomerAccountNumbers() {
        List<Account> accounts = getLoggedInCustomerAccounts();
        return accounts.stream()
                .map(Account::getAccountNumber)
                .toArray(String[]::new);
    }

    /**
     * Get logged-in customer's full name for display
     * 
     * @return customer's full name if logged in, "Customer" otherwise
     */
    public String getLoggedInCustomerName() {
        Customer customer = authContext.getLoggedInCustomer();
        return customer != null ? customer.getFullName() : "Customer";
    }

    /**
     * Get account details as strings for presentation layer
     * 
     * @return 2D array where each row is [accountNumber, accountType, balance,
     *         currency, status]
     *         Returns empty array if not logged in
     */
    public String[][] getLoggedInCustomerAccountsAsStrings() {
        List<Account> accounts = getLoggedInCustomerAccounts();
        String[][] accountsData = new String[accounts.size()][5];

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            accountsData[i][0] = account.getAccountNumber();
            accountsData[i][1] = account.getAccountType();
            accountsData[i][2] = String.format("%.2f", account.getBalance());
            accountsData[i][3] = account.getCurrency();
            accountsData[i][4] = account.getStatus();
        }

        return accountsData;
    }

    /**
     * Get the account number of the selected account by index
     * 
     * @param accountIndex the index in the accounts list
     * @return account number if valid index, null otherwise
     */
    public String getLoggedInCustomerAccountNumber(int accountIndex) {
        List<Account> accounts = getLoggedInCustomerAccounts();
        if (accountIndex >= 0 && accountIndex < accounts.size()) {
            return accounts.get(accountIndex).getAccountNumber();
        }
        return null;
    }

    /**
     * Logout the current customer
     */
    public void logout() {
        authContext.logout();
    }

    /**
     * Check if a customer is logged in
     * 
     * @return true if customer is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return authContext.isLoggedIn();
    }

    /**
     * Check if the logged-in user has the ADMIN role
     * 
     * @return true if user is an admin/teller, false otherwise
     */
    public boolean isAdmin() {
        return authContext.isAdmin();
    }

    /**
     * Get the role of the logged-in user
     * 
     * @return "CUSTOMER" or "ADMIN", null if not logged in
     */
    public String getUserRole() {
        return authContext.getRole();
    }
}
