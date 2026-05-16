package com.bms.api.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bms.api.dto.LoanCancelResponse;
import com.bms.api.dto.LoanDecisionRequest;
import com.bms.api.dto.LoanDecisionResponse;
import com.bms.api.dto.LoanResponse;
import com.bms.api.dto.SubmitLoanRequest;
import com.bms.api.dto.SubmitLoanResponse;
import com.bms.api.mapper.LoanMapper;
import com.bms.domain.entity.Loan;
import com.bms.service.LoanApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class LoanController {
    private final LoanApplicationService loanApplicationService;

    public LoanController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @GetMapping("/customers/{customerId}/loans")
    public List<LoanResponse> getCustomerLoans(@PathVariable("customerId") int customerId) {
        if (customerId <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_CUSTOMER_ID",
                    "Customer ID must be greater than zero");
        }
        return loanApplicationService.getLoansForCustomer(customerId).stream()
                .map(LoanMapper::toResponse)
                .toList();
    }

    @PostMapping("/loans")
    public ResponseEntity<SubmitLoanResponse> submitLoan(@Valid @RequestBody SubmitLoanRequest request) {
        LoanApplicationService.LoanResult result =
                loanApplicationService.submitLoanApplication(
                        request.customerId(),
                        request.amount(),
                        request.loanType(),
                        request.termMonths(),
                        request.loanPurpose());
        if (result.getLoanId() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "LOAN_APPLICATION_FAILED",
                    "Loan application could not be submitted");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmitLoanResponse(result.getLoanId(), result.getStatus(),
                        "Loan application submitted successfully"));
    }

    @PutMapping("/loans/{loanId}/decision")
    public LoanDecisionResponse decideLoan(@PathVariable("loanId") int loanId,
            @Valid @RequestBody LoanDecisionRequest request) {
        Loan loan = requireLoan(loanId);
        if (!"PENDING".equals(loan.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "LOAN_NOT_PENDING",
                    "Only pending loan applications can be approved or rejected");
        }

        String decision = request.decision().trim().toUpperCase(Locale.ROOT);
        if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_LOAN_DECISION",
                    "Decision must be APPROVED or REJECTED");
        }

        boolean updated = loanApplicationService.decideLoan(loanId, decision, request.adminId());
        if (!updated) {
            throw new ApiException(HttpStatus.CONFLICT, "LOAN_DECISION_NOT_UPDATED",
                    "Loan decision could not be updated");
        }

        String message = "APPROVED".equals(decision) ? "Loan approved" : "Loan rejected";
        return new LoanDecisionResponse(loanId, decision, message);
    }

    @DeleteMapping("/loans/{loanId}")
    public LoanCancelResponse cancelLoan(@PathVariable("loanId") int loanId) {
        Loan loan = requireLoan(loanId);
        if (!"PENDING".equals(loan.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "LOAN_NOT_PENDING",
                    "Only pending loan applications can be cancelled");
        }

        boolean cancelled = loanApplicationService.cancelPendingLoan(loanId);
        if (!cancelled) {
            throw new ApiException(HttpStatus.CONFLICT, "LOAN_NOT_CANCELLED",
                    "Pending loan application could not be cancelled");
        }
        return new LoanCancelResponse(loanId, "CANCELLED", "Pending loan application cancelled");
    }

    private Loan requireLoan(int loanId) {
        if (loanId <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_LOAN_ID",
                    "Loan ID must be greater than zero");
        }
        Loan loan = loanApplicationService.getLoanDetails(loanId);
        if (loan == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "LOAN_NOT_FOUND", "Loan was not found");
        }
        return loan;
    }
}
