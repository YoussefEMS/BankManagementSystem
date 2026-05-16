package com.bms.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bms.api.dto.CustomerOnboardingRequest;
import com.bms.api.dto.CustomerOnboardingResponse;
import com.bms.service.CustomerOnboardingWorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    private final CustomerOnboardingWorkflowService onboardingWorkflowService;

    public WorkflowController(CustomerOnboardingWorkflowService onboardingWorkflowService) {
        this.onboardingWorkflowService = onboardingWorkflowService;
    }

    @PostMapping("/customer-onboarding")
    public ResponseEntity<CustomerOnboardingResponse> onboardCustomer(
            @Valid @RequestBody CustomerOnboardingRequest request) {
        CustomerOnboardingWorkflowService.OnboardingResult result =
                onboardingWorkflowService.onboard(
                        new CustomerOnboardingWorkflowService.CustomerInput(
                                request.customer().fullName(),
                                request.customer().email(),
                                request.customer().nationalId(),
                                request.customer().password(),
                                request.customer().phone(),
                                request.customer().address()),
                        new CustomerOnboardingWorkflowService.AccountInput(
                                request.account().accountType(),
                                request.account().initialDeposit(),
                                request.account().currency()),
                        request.loanApplication() != null
                                ? new CustomerOnboardingWorkflowService.LoanInput(
                                        request.loanApplication().amount(),
                                        request.loanApplication().termMonths(),
                                        request.loanApplication().loanPurpose(),
                                        request.loanApplication().loanType())
                                : null);

        if (!result.isSuccess() && "PARTIALLY_COMPLETED".equals(result.getWorkflowStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CustomerOnboardingResponse(result.getCustomerId(),
                            result.getAccountNumber(), result.getLoanId(), result.getWorkflowStatus()));
        }
        if (!result.isSuccess()) {
            throw mapWorkflowFailure(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomerOnboardingResponse(result.getCustomerId(),
                        result.getAccountNumber(), result.getLoanId(), result.getWorkflowStatus()));
    }

    private static ApiException mapWorkflowFailure(CustomerOnboardingWorkflowService.OnboardingResult result) {
        HttpStatus status = switch (result.getCode()) {
            case "DUPLICATE_EMAIL", "DUPLICATE_NATIONAL_ID" -> HttpStatus.CONFLICT;
            case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ApiException(status, result.getCode(), result.getMessage());
    }
}
