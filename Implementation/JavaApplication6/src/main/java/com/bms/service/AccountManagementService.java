package com.bms.service;

import com.bms.domain.controller.AccountStatusUpdater;
import com.bms.domain.controller.CustomerProfileController;
import com.bms.domain.entity.Account;
import com.bms.event.AccountClosedEvent;
import com.bms.event.AccountStatusChangedEvent;
import com.bms.event.CustomerProfileCreatedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.service.base.ApplicationService;

public class AccountManagementService extends ApplicationService {
    private final CustomerProfileController customerProfileController;
    private final AccountStatusUpdater accountStatusUpdater;
    private final AccountDAO accountDAO;

    public AccountManagementService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public AccountManagementService(PersistenceProvider factory) {
        super();
        this.customerProfileController = new CustomerProfileController(factory);
        this.accountStatusUpdater = new AccountStatusUpdater(factory);
        this.accountDAO = factory.createAccountDAO();
    }

    public AccountManagementService(PersistenceProvider factory, EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.customerProfileController = new CustomerProfileController(factory);
        this.accountStatusUpdater = new AccountStatusUpdater(factory);
        this.accountDAO = factory.createAccountDAO();
    }

    public int createCustomerProfile(String fullName, String email, String mobilePhone,
            String address, String nationalID) {
        int customerId = customerProfileController.createCustomerProfile(
                fullName, email, mobilePhone, address, nationalID);
        if (customerId > 0) {
            publish(new CustomerProfileCreatedEvent(customerId, trim(fullName), trim(email)));
        }
        return customerId;
    }

    public boolean updateAccountStatus(int adminId, String accountNo, String newStatus) {
        Account existingAccount = accountNo != null ? accountDAO.findByAccountNo(accountNo.trim()) : null;
        String previousStatus = existingAccount != null ? existingAccount.getStatus() : null;

        boolean updated = accountStatusUpdater.updateAccountStatus(adminId, accountNo, newStatus);
        if (updated) {
            String normalizedAccountNo = accountNo.trim();
            publish(new AccountStatusChangedEvent(normalizedAccountNo, previousStatus, newStatus, adminId));
            if ("CLOSED".equals(newStatus)) {
                publish(new AccountClosedEvent(normalizedAccountNo, adminId));
            }
        }
        return updated;
    }

    private static String trim(String value) {
        return value != null ? value.trim() : "";
    }
}
