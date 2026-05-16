package com.bms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import com.bms.domain.controller.AccountStatusUpdater;
import com.bms.domain.controller.CustomerProfileController;
import com.bms.domain.entity.Account;
import com.bms.domain.entity.Customer;
import com.bms.domain.entity.Transaction;
import com.bms.event.AccountClosedEvent;
import com.bms.event.AccountCreatedEvent;
import com.bms.event.AccountStatusChangedEvent;
import com.bms.event.CustomerProfileCreatedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.CustomerDAO;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.TransactionDAO;
import com.bms.service.base.ApplicationService;

public class AccountManagementService extends ApplicationService {
    private final CustomerProfileController customerProfileController;
    private final AccountStatusUpdater accountStatusUpdater;
    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;
    private final TransactionDAO transactionDAO;

    public static class CustomerAccountResult {
        private final boolean success;
        private final String code;
        private final String message;
        private final int customerId;
        private final String accountNumber;
        private final String status;

        public CustomerAccountResult(boolean success, String code, String message,
                int customerId, String accountNumber, String status) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.customerId = customerId;
            this.accountNumber = accountNumber;
            this.status = status;
        }

        public static CustomerAccountResult created(int customerId, String accountNumber) {
            return new CustomerAccountResult(true, "CREATED", "Customer profile and account created",
                    customerId, accountNumber, "CREATED");
        }

        public static CustomerAccountResult failed(String code, String message) {
            return new CustomerAccountResult(false, code, message, -1, null, "FAILED");
        }

        public boolean isSuccess() {
            return success;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public int getCustomerId() {
            return customerId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getStatus() {
            return status;
        }
    }

    public AccountManagementService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public AccountManagementService(PersistenceProvider factory) {
        super();
        this.customerProfileController = new CustomerProfileController(factory);
        this.accountStatusUpdater = new AccountStatusUpdater(factory);
        this.accountDAO = factory.createAccountDAO();
        this.customerDAO = factory.createCustomerDAO();
        this.transactionDAO = factory.createTransactionDAO();
    }

    public AccountManagementService(PersistenceProvider factory, EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.customerProfileController = new CustomerProfileController(factory);
        this.accountStatusUpdater = new AccountStatusUpdater(factory);
        this.accountDAO = factory.createAccountDAO();
        this.customerDAO = factory.createCustomerDAO();
        this.transactionDAO = factory.createTransactionDAO();
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

    public CustomerAccountResult createCustomerProfile(String fullName, String email, String mobilePhone,
            String address, String nationalID, String password, String accountType,
            BigDecimal initialDeposit, String currency) {
        if (isBlank(fullName) || isBlank(email) || isBlank(nationalID) || isBlank(password)) {
            return CustomerAccountResult.failed("VALIDATION_ERROR",
                    "Full name, email, national ID, and password are required");
        }
        if (!email.contains("@")) {
            return CustomerAccountResult.failed("VALIDATION_ERROR", "Email address is invalid");
        }
        if (isBlank(accountType)) {
            return CustomerAccountResult.failed("VALIDATION_ERROR", "Account type is required");
        }
        BigDecimal openingBalance = initialDeposit != null ? initialDeposit : BigDecimal.ZERO;
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            return CustomerAccountResult.failed("VALIDATION_ERROR", "Initial deposit cannot be negative");
        }

        String normalizedEmail = trim(email).toLowerCase(Locale.ROOT);
        String normalizedNationalId = trim(nationalID);
        if (customerDAO.existsByEmail(normalizedEmail)) {
            return CustomerAccountResult.failed("DUPLICATE_EMAIL", "A customer with this email already exists");
        }
        if (customerDAO.existsByNationalId(normalizedNationalId)) {
            return CustomerAccountResult.failed("DUPLICATE_NATIONAL_ID",
                    "A customer with this national ID already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(trim(fullName));
        customer.setEmail(normalizedEmail);
        customer.setPhoneNumber(trim(mobilePhone));
        customer.setAddress(trim(address));
        customer.setNationalID(normalizedNationalId);
        customer.setPassword(password);
        customer.setRole("CUSTOMER");
        customer.setTier("SILVER");
        customer.setStatus("ACTIVE");

        int customerId = customerDAO.insert(customer);
        if (customerId <= 0) {
            return CustomerAccountResult.failed("CUSTOMER_CREATE_FAILED", "Customer profile could not be created");
        }

        String accountNumber = generateAccountNumber(customerId);
        Account account = new Account(
                accountNumber,
                customerId,
                trim(accountType).toUpperCase(Locale.ROOT),
                openingBalance,
                normalizeCurrency(currency),
                "ACTIVE",
                LocalDateTime.now());

        if (!accountDAO.insert(account)) {
            return CustomerAccountResult.failed("ACCOUNT_CREATE_FAILED", "Account could not be created");
        }

        if (openingBalance.compareTo(BigDecimal.ZERO) > 0) {
            transactionDAO.insert(buildOpeningDeposit(account, customer.getFullName()));
        }

        publish(new CustomerProfileCreatedEvent(customerId, customer.getFullName(), normalizedEmail));
        publish(new AccountCreatedEvent(accountNumber, customerId, account.getAccountType()));
        return CustomerAccountResult.created(customerId, accountNumber);
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

    public boolean closeAccount(int adminId, String accountNo) {
        return updateAccountStatus(adminId, accountNo, "CLOSED");
    }

    private static String trim(String value) {
        return value != null ? value.trim() : "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalizeCurrency(String currency) {
        String normalized = trim(currency);
        return normalized.isEmpty() ? "EGP" : normalized.toUpperCase(Locale.ROOT);
    }

    private static Transaction buildOpeningDeposit(Account account, String performedBy) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(account.getAccountNumber());
        transaction.setType("DEPOSIT");
        transaction.setAmount(account.getBalance());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformedBy(performedBy);
        transaction.setNote("Initial account opening deposit");
        transaction.setBalanceAfter(account.getBalance());
        transaction.setReferenceCode("DEP-API-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        return transaction;
    }

    private static String generateAccountNumber(int customerId) {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return String.format(Locale.ROOT, "EG-API-%06d-%s", customerId, suffix);
    }
}
