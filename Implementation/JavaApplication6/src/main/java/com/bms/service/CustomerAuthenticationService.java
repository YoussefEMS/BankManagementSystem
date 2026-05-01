package com.bms.service;

import java.util.List;

import com.bms.domain.controller.AuthenticationController;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Customer;
import com.bms.event.CustomerAuthenticatedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.service.base.ApplicationService;

public class CustomerAuthenticationService extends ApplicationService {
    private final AuthenticationController authenticationController;

    public CustomerAuthenticationService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public CustomerAuthenticationService(PersistenceProvider factory) {
        super();
        this.authenticationController = new AuthenticationController(factory);
    }

    public CustomerAuthenticationService(AuthenticationController authenticationController,
            EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.authenticationController = authenticationController;
    }

    public Customer authenticateCustomer(String email, String password) {
        Customer customer = authenticationController.authenticateCustomer(email, password);
        if (customer != null) {
            publish(new CustomerAuthenticatedEvent(customer.getCustomerId(), customer.getEmail(), customer.getRole()));
        }
        return customer;
    }

    public boolean authenticate(String email, String password) {
        return authenticateCustomer(email, password) != null;
    }

    public int getLoggedInCustomerId() {
        return authenticationController.getLoggedInCustomerId();
    }

    public Customer getLoggedInCustomer() {
        return authenticationController.getLoggedInCustomer();
    }

    public List<Account> getLoggedInCustomerAccounts() {
        return authenticationController.getLoggedInCustomerAccounts();
    }

    public String[] getLoggedInCustomerAccountNumbers() {
        return authenticationController.getLoggedInCustomerAccountNumbers();
    }

    public String getLoggedInCustomerName() {
        return authenticationController.getLoggedInCustomerName();
    }

    public String[][] getLoggedInCustomerAccountsAsStrings() {
        return authenticationController.getLoggedInCustomerAccountsAsStrings();
    }

    public String getLoggedInCustomerAccountNumber(int accountIndex) {
        return authenticationController.getLoggedInCustomerAccountNumber(accountIndex);
    }

    public void logout() {
        authenticationController.logout();
    }

    public boolean isLoggedIn() {
        return authenticationController.isLoggedIn();
    }

    public boolean isAdmin() {
        return authenticationController.isAdmin();
    }

    public String getUserRole() {
        return authenticationController.getUserRole();
    }

    public String getLoggedInCustomerAccountTypeByNumber(String accountNumber) {
        return authenticationController.getLoggedInCustomerAccountTypeByNumber(accountNumber);
    }
}
