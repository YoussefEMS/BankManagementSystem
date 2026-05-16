package com.bms.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bms.api.dto.CreateCustomerRequest;
import com.bms.api.dto.CreateCustomerResponse;
import com.bms.service.AccountManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final AccountManagementService accountManagementService;

    public CustomerController(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    @PostMapping
    public ResponseEntity<CreateCustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        AccountManagementService.CustomerAccountResult result =
                accountManagementService.createCustomerProfile(
                        request.fullName(),
                        request.email(),
                        request.phone(),
                        request.address(),
                        request.nationalId(),
                        request.password(),
                        request.accountType(),
                        request.initialDeposit(),
                        request.currency());

        if (!result.isSuccess()) {
            throw mapCreateFailure(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateCustomerResponse(result.getCustomerId(),
                        result.getAccountNumber(), result.getStatus()));
    }

    private static ApiException mapCreateFailure(AccountManagementService.CustomerAccountResult result) {
        HttpStatus status = switch (result.getCode()) {
            case "DUPLICATE_EMAIL", "DUPLICATE_NATIONAL_ID" -> HttpStatus.CONFLICT;
            case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ApiException(status, result.getCode(), result.getMessage());
    }
}
