package com.bms.api.controller;

import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bms.api.dto.AccountResponse;
import com.bms.api.dto.AccountStatusResponse;
import com.bms.api.dto.UpdateAccountStatusRequest;
import com.bms.api.mapper.AccountMapper;
import com.bms.domain.entity.Account;
import com.bms.service.AccountManagementService;
import com.bms.service.AccountQueryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private static final Set<String> ACCOUNT_STATUSES = Set.of("ACTIVE", "FROZEN", "CLOSED");

    private final AccountQueryService accountQueryService;
    private final AccountManagementService accountManagementService;

    public AccountController(AccountQueryService accountQueryService,
            AccountManagementService accountManagementService) {
        this.accountQueryService = accountQueryService;
        this.accountManagementService = accountManagementService;
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable("accountNumber") String accountNumber) {
        Account account = requireAccount(accountNumber);
        return AccountMapper.toResponse(account);
    }

    @PutMapping("/{accountNumber}/status")
    public AccountStatusResponse updateStatus(@PathVariable("accountNumber") String accountNumber,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        requireAdminIfRoleProvided(request.role());
        Account account = requireAccount(accountNumber);
        String status = normalizeStatus(request.status());
        if (!ACCOUNT_STATUSES.contains(status)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ACCOUNT_STATUS",
                    "Status must be ACTIVE, FROZEN, or CLOSED");
        }

        boolean updated = accountManagementService.updateAccountStatus(
                request.actorId() != null ? request.actorId() : -1,
                account.getAccountNumber(),
                status);
        if (!updated) {
            throw new ApiException(HttpStatus.CONFLICT, "ACCOUNT_STATUS_NOT_UPDATED",
                    "Account status could not be updated");
        }
        return new AccountStatusResponse(account.getAccountNumber(), status, "Account status updated");
    }

    @DeleteMapping("/{accountNumber}")
    public AccountStatusResponse closeAccount(@PathVariable("accountNumber") String accountNumber,
            @RequestParam(name = "actorId", required = false) Integer actorId,
            @RequestParam(name = "role", required = false) String role) {
        requireAdminIfRoleProvided(role);
        Account account = requireAccount(accountNumber);

        boolean closed = accountManagementService.closeAccount(
                actorId != null ? actorId : -1,
                account.getAccountNumber());
        if (!closed) {
            throw new ApiException(HttpStatus.CONFLICT, "ACCOUNT_NOT_CLOSED",
                    "Account could not be closed");
        }
        return new AccountStatusResponse(account.getAccountNumber(), "CLOSED", "Account closed successfully");
    }

    private Account requireAccount(String accountNumber) {
        Account account = accountQueryService.getAccount(accountNumber);
        if (account == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "Account was not found");
        }
        return account;
    }

    private static String normalizeStatus(String status) {
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private static void requireAdminIfRoleProvided(String role) {
        if (role != null && !role.isBlank() && !"ADMIN".equals(role.trim().toUpperCase(Locale.ROOT))) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ADMIN_ROLE_REQUIRED",
                    "This operation requires an ADMIN actor");
        }
    }
}
